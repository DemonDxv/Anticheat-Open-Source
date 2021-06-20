package me.rhys.anticheat.base.processor.impl;

import lombok.Getter;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.impl.processors.*;
import me.rhys.anticheat.base.user.User;

import java.util.LinkedList;
import java.util.List;

@Getter
public class ProcessorManager {
    private final User user;
    private final List<Processor> processors = new LinkedList<>();

    public ProcessorManager(User user) {
        this.user = user;
    }

    public void setup() {
        this.processors.add(new MovementProcessor());
        this.processors.add(new ConnectionProcessor());
        this.processors.add(new CombatProcessor());
        this.processors.add(new ActionProcessor());
        this.processors.add(new PotionProcessor());
        this.processors.add(new ElytraProcessor());
        this.processors.add(new PredictionProcessor());

        this.processors.forEach(processor -> processor.setup(this.user));
    }

    public <T> Processor forClass(Class<? extends Processor> aClass) {
        return processors.stream()
                .filter(module -> module.getClass().equals(aClass)).findFirst().orElse(null);
    }
}
