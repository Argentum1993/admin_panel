package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        return players != null
                ? new ResponseEntity<>(players, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rest/players/count")
    public ResponseEntity<Integer> readCount(@RequestParam Map<String,String> allParams){
        return new ResponseEntity<>(playerService.count(allParams), HttpStatus.OK);
    }

    @GetMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> read(@PathVariable(name = "id") Long id) {
        if (id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Player player = playerService.getPlayer(id);

        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/rest/players/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        final boolean deleted = playerService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/rest/players")
    public ResponseEntity<Player> create(@RequestBody Player player){
        final Player playerEntity = playerService.create(player);
        return playerEntity != null
                ? new ResponseEntity<>(playerEntity, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
