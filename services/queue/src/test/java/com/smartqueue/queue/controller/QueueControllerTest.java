package com.smartqueue.queue.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.queue.constant.EventType;
import com.smartqueue.queue.dto.CreateQueueRequest;
import com.smartqueue.queue.dto.JoinQueueRequest;
import com.smartqueue.queue.dto.QueueDto;
import com.smartqueue.queue.entity.Queue;
import com.smartqueue.queue.entity.QueueEvent;
import com.smartqueue.queue.repository.QueueRepository;
import com.smartqueue.queue.testUtils.TestContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QueueControllerTest extends TestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QueueRepository queueRepository;

    private static String baseURL;

    private static KafkaConsumer<String, QueueEvent> consumer;

    @AfterAll
    static void afterAll() {
        consumer.close();
    }

    @BeforeEach
    void beforeEach() {
        queueRepository.deleteAll();
    }

    @BeforeAll
    static void setUp() {
        baseURL = "/api/v1/queue";

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, QueueEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");


        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of("test-topic"));
    }

    @Test
    void createQueue_Success() throws Exception {
        CreateQueueRequest request = new CreateQueueRequest("Test queue", "test");

        mockMvc.perform(post(baseURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()));

        assertThat(queueRepository.findAll()).hasSize(1);
    }

    @Test
    void joinQueue_Success() throws Exception {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest("Test queue", "test");
        String response = mockMvc.perform(post(baseURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createQueueRequest)))
                .andReturn().getResponse().getContentAsString();

        Queue queue = objectMapper.readValue(response, Queue.class);

        JoinQueueRequest request = new JoinQueueRequest(queue.getId());

        mockMvc.perform(post(baseURL + "/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-User-Id", 1))
                .andExpect(status().isOk());

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, QueueEvent> records =
                    consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, QueueEvent> record : records) {
                QueueEvent event = record.value();
                assertThat(event.getQueueId()).isEqualTo(queue.getId());
                assertThat(event.getType()).isEqualTo(EventType.JOINED);
            }
        });
    }

    @Test
    void callNext_Success() throws Exception {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest("Test queue", "test");

        QueueDto queue = objectMapper.readValue(mockMvc.perform(post(baseURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createQueueRequest)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
                new TypeReference<QueueDto>() {});

        JoinQueueRequest joinQueueRequest = new JoinQueueRequest(queue.getId());

        mockMvc.perform(post(baseURL + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinQueueRequest))
                        .header("X-User-Id", 1))
                .andExpect(status().isOk());

        mockMvc.perform(patch(baseURL + "/call-next/" + queue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.queueId").value(queue.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void callNext_Failure() throws Exception {
        mockMvc.perform(patch(baseURL + "/call-next/" + 444))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("Queue with ID " + 444 + " was not found" +
                        " or there are no active entries in the queue."));
    }

}