package com.internship.skynet.model;

import java.util.EnumMap;
import java.util.Map;

public class Robot {

    private final Map<PartType, Boolean> parts;


    public Robot() {
        this.parts = new EnumMap<>(PartType.class);
        for (PartType partType : PartType.values()) {
            this.parts.put(partType, false);
        }
    }


    public boolean addPart(Part part) {
        if (part == null || parts.get(part.type()) == null) {
            return false;
        }
        parts.put(part.type(), true);
        return true;
    }


    public boolean isComplete() {
        return parts.values().stream().allMatch(Boolean::booleanValue);
    }


    public void reset() {
        for (PartType partType : PartType.values()) {
            parts.put(partType, false);
        }
    }


    @Override
    public String toString() {
        return "Robot{" +
                "head=" + parts.get(PartType.HEAD) +
                ", torso=" + parts.get(PartType.TORSO) +
                ", hand=" + parts.get(PartType.HAND) +
                ", foot=" + parts.get(PartType.FOOT) +
                '}';
    }
}

