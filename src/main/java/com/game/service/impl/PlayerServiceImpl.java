package com.game.service.impl;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import com.game.service.impl.filters.PlayerFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
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

    @Override
    public Integer count(Map<String, String> params) {
        return (int) playerRepository.count(PlayerFilters.createFiltersFromParams(params));
    }

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

    @Override
    public boolean delete(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Player getPlayer(Long id) {
        if (playerRepository.existsById(id))
            return playerRepository.findById(id).get();
        else
            return null;
    }

    @Override
    public Player create(Player player) {
        if (!validRawPlayer(player)){
            return null;
        }

        Integer experience = player.getExperience();
        Integer level = calculateLevel(experience);

        player.setLevel(level);
        player.setUntilNextLevel(calculateUntilNextLevel(level, experience));

        player.setId(0l);
        player = playerRepository.save(player);
        return player;
    }

    private Integer calculateLevel(Integer experience){
        return (int) Math.round((Math.sqrt(2500 + 200 * experience) - 50) / 100);
    }

    private Integer calculateUntilNextLevel(Integer level, Integer experience){
        return 50 * (level + 1) * (level + 2) - experience;
    }

    private boolean validRawPlayer(Player player){
        if (player.getName() == null || player.getName().isEmpty() || player.getName().length() > 12)
            return false;
        if (player.getTitle() == null || player.getTitle().length() > 30)
            return false;
        if (player.getRace() == null)
            return false;
        if (player.getProfession() == null)
            return false;


        Calendar calendar = Calendar.getInstance();
        Date birthday = player.getBirthday();
        calendar.set(Calendar.YEAR, 2000);
        Date minDate = calendar.getTime();
        calendar.set(Calendar.YEAR, 3000);
        Date maxDate = calendar.getTime();
        if (birthday == null || birthday.before(minDate) || birthday.after(maxDate))
            return false;
        if (player.getExperience() == null || player.getExperience() < 0 || player.getExperience() >10_000_000)
            return false;
        return true;
    }
}
