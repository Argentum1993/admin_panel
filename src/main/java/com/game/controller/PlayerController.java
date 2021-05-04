package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/rest/players")
    public ResponseEntity<List<Player>> read(
            @RequestParam Map<String,String> allParams,
            @RequestParam(required = false) PlayerOrder order)
    {
        String orderField = order != null ? order.getFieldName() : PlayerOrder.ID.getFieldName();
        final List<Player> players = playerService.getPlayers(allParams, orderField);

        return players != null &&  !players.isEmpty()
                ? new ResponseEntity<>(players, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rest/players/count")
    public ResponseEntity<Integer> readCount(@RequestParam Map<String,String> allParams){
        return new ResponseEntity<>(playerService.count(allParams), HttpStatus.OK);
    }

}
