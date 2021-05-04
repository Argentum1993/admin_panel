package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PlayerService {
    List<Player>    getPlayers(Map<String, String> params, String order);
    Integer         count(Map<String, String> params);
}
