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

        player = playerRepository.save(player);
        return player;
    }

    @Override
    public Player update(Long id, Player updates) {
        Player player = getPlayer(id);

        if (player != null){
            if (updates.getName() != null)
                player.setName(updates.getName());
            if (updates.getTitle() != null)
                player.setTitle(updates.getTitle());
            if (updates.getRace() != null)
                player.setRace(updates.getRace());
            if (updates.getProfession() != null)
                player.setProfession(updates.getProfession());
            if (updates.getBirthday() != null)
                player.setBirthday(updates.getBirthday());
            if (updates.getBanned() != null)
                player.setBanned(updates.getBanned());
            if (updates.getExperience() != null){
                Integer exp = updates.getExperience();
                player.setExperience(exp);
                player.setLevel(calculateLevel(exp));
                player.setUntilNextLevel(calculateUntilNextLevel(player.getLevel(), exp));
            }
            return playerRepository.save(player);
        }
        return null;
    }

    private Integer calculateLevel(Integer experience){
        return (int) (Math.sqrt(2500 + 200 * experience) - 50) / 100;
    }

    private Integer calculateUntilNextLevel(Integer level, Integer experience){
        return 50 * (level + 1) * (level + 2) - experience;
    }

    @Override
    public boolean validUpdates(Player updates){
        if (updates.getName() != null && !validName(updates.getName()))
            return false;
        if (updates.getTitle() != null && !validTitle(updates.getTitle()))
            return false;
        if (updates.getBirthday() != null && !validBirthday(updates.getBirthday()))
            return false;
        if (updates.getExperience() != null && !validExperience(updates.getExperience()))
            return false;
        return true;
    }

    private boolean validRawPlayer(Player player){
        if (!validName(player.getName()) || !validTitle(player.getTitle())
                || player.getRace() == null || player.getProfession() == null
                || !validBirthday(player.getBirthday()) || !validExperience(player.getExperience()))
            return false;
        return true;
    }

    private boolean validName(String name){
        if (name == null || name.isEmpty() || name.length() > 12)
            return false;
        return true;
    }

    private boolean validTitle(String title){
        if (title == null || title.length() > 30)
            return false;
        return true;
    }

    private boolean validBirthday(Date birthday){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        Date minDate = calendar.getTime();
        calendar.set(Calendar.YEAR, 3000);
        Date maxDate = calendar.getTime();
        if (birthday == null || birthday.before(minDate) || birthday.after(maxDate))
            return false;
        return true;
    }

    private boolean validExperience(Integer experience){
        if (experience == null || experience < 0 || experience > 10_000_000)
            return false;
        return true;
    }
}
