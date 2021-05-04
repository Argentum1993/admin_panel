package com.game.service.impl;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import com.game.service.impl.filters.PlayerFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

//    @Override
//    public List<Player> getPlayers(Integer pageNumber, Integer pageSize, PlayerOrder order) {
//        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
//
//        Page<Player> pagedResult = playerRepository.findAll(, paging);
//
//        if (pagedResult.hasContent()){
//            return pagedResult.getContent();
//        } else {
//            return new ArrayList<>();
//        }
//    }

    @Override
    public Integer count(Map<String, String> params) {
        return (int) playerRepository.count(PlayerFilters.createFiltersFromParams(params));
    }

    // TODO: create filter by params
    @Override
    public List<Player> getPlayers(Map<String, String> params, String order) {
        Integer pageNumber = 0;
        Integer pageSize = 3;
        if (params.containsKey("pageNumber")) {
            try {
                pageNumber = Integer.parseInt(params.get("pageNumber"));
                if (pageNumber < 0)
                    pageNumber = 0;
            } catch (Exception e){
                pageNumber = 0;
            }
        }
        if (params.containsKey("pageSize")){
            try {
                pageSize = Integer.parseInt(params.get("pageSize"));
            } catch (Exception e){
                pageSize = 3;
            }
        }
        return playerRepository.findAll(
                PlayerFilters.createFiltersFromParams(params),
                PageRequest.of(pageNumber, pageSize, Sort.by(order)))
                .stream()
                .collect(Collectors.toList());
    }
}
