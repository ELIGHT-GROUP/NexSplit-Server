package org.bhanuka.sse_example.service;

import lombok.Getter;
import org.bhanuka.sse_example.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TaskService {
    @Getter
    private final List<Task> tasks = new ArrayList<>();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private Long idCounter = 1L;

    public Task addTask(String description) {
        Task task = new Task(idCounter++, description);
        tasks.add(task);
        notifyClients();
        return task;
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        sendInitialData(emitter);
        return emitter;
    }

    private void sendInitialData(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("tasks").data(tasks));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private void notifyClients() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("tasks").data(tasks));
            } catch (IOException e) {
                emitters.remove(emitter);
                emitter.completeWithError(e);
            }
        }
    }
}