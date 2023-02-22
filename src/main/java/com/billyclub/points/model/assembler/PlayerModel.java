package com.billyclub.points.model.assembler;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
//@Getter
//@Setter
@JsonRootName(value = "player")
@Relation(collectionRelation = "players")
public class PlayerModel extends RepresentationModel<PlayerModel> {
    private Long id;
    private String name;
    private Integer pointsToPull;
    private Integer pointsThisEvent;
    private LocalDateTime timeEntered;
    private EventModel event;
}
