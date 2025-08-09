package org.satellite.dev.progiple.sateplanet.planets;

import lombok.Getter;

import java.util.List;

public record GravitationLevel(@Getter int level, @Getter List<String> effectList) {
}
