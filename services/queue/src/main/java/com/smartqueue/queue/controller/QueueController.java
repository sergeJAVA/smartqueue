package com.smartqueue.queue.controller;

import com.smartqueue.queue.dto.CreateQueueRequest;
import com.smartqueue.queue.dto.JoinQueueRequest;
import com.smartqueue.queue.dto.QueueDto;
import com.smartqueue.queue.dto.QueueEntryDto;
import com.smartqueue.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping
    public ResponseEntity<QueueDto> createQueue(@RequestBody CreateQueueRequest request) {
        QueueDto queueDto = queueService.createQueue(request);
        return ResponseEntity.ok(queueDto);
    }

    @PostMapping("/join")
    public ResponseEntity<QueueEntryDto> joinQueue(@RequestBody JoinQueueRequest request,
                                                   @RequestHeader("X-User-Id") String userId) {
        QueueEntryDto queueEntryDto = queueService.joinQueue(request.getQueueId(), Long.parseLong(userId));
        return ResponseEntity.ok(queueEntryDto);
    }

}
