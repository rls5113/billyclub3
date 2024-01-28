package com.billyclub.points.service.impl;

import com.billyclub.points.dto.CoverallDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.*;
import com.billyclub.points.repository.CoverallRepository;
import com.billyclub.points.service.CoverallPlayerService;
import com.billyclub.points.service.CoverallService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoverallServiceImpl implements CoverallService {
    public final CoverallRepository repository;
    private final CoverallPlayerService coverallPlayerService;
    @Autowired
    public CoverallServiceImpl(CoverallRepository repository, CoverallPlayerService coverallPlayerService) {
        this.repository=repository;
        this.coverallPlayerService = coverallPlayerService;
    }

    @Override
    public Coverall addPlayerToCoverall(Long coverallId, Long playerId) {
        Coverall coverall = findById(coverallId);
        CoverallPlayer player = coverallPlayerService.findById(playerId);
        if(!coverall.isPlayerInCoverall(player.getName())) {
            coverall.addPlayer(player);
        }
        coverall.setMoney(coverall.getMoney() + 20);
        return repository.save(coverall);

    }

    @Override
    public List<Coverall> findOpenCoveralls() {
        return repository.findOpenCoveralls();
    }

    @Override
    public Event processEvent(Event event) {
        List<Coverall> coveralls = findOpenCoveralls();
        for(Coverall coverall: coveralls){
            List<Player> playersInCoverall = event.getPlayers().stream()
                    .filter(e -> coverall.getPlayers().stream()
                            .anyMatch(c1 -> c1.getName().equals(e.getName())))
                    .collect(Collectors.toList());
            System.out.println(playersInCoverall.toString());
            //add $1 for each Player
            List<CoverallPlayer> playersAnte = coverall.getPlayers().stream()
                    .filter(cp -> (!cp.getIsFirstPlay()) && event.getPlayers().stream().anyMatch(ep -> ep.getName().equals(cp.getName())))
                    .collect(Collectors.toList());
            System.out.println("ante size" + playersAnte.size());
            coverall.setMoney(coverall.getMoney() + playersAnte.size());

            List<CoverallPlayer> playersFirstPlay = coverall.getPlayers().stream()
                    .filter(cp -> (cp.getIsFirstPlay()) && event.getPlayers().stream().anyMatch(ep -> ep.getName().equals(cp.getName())))
                    .collect(Collectors.toList());
            for(CoverallPlayer cp : playersFirstPlay){
                cp.setIsFirstPlay(Boolean.FALSE);
            }
            System.out.println("ante size" + playersAnte.size());

            List<Hole> newHoles = new ArrayList<>();

            for(Integer holenum : CoverallDto.holes){
                String sHole = String.valueOf(holenum);

                List<Player> eagledThisHole = playersInCoverall.stream()
                        .filter(b -> b.getEagles().contains(sHole))
                        .collect(Collectors.toList());
                System.out.println(eagledThisHole);

                List<Player> birdiedThisHole = playersInCoverall.stream()
                        .filter(b -> b.getBirdies().contains(sHole))
                        .collect(Collectors.toList());
                System.out.println(birdiedThisHole);

                Optional<Hole> thisHole = coverall.getHoles().stream()
                        .filter(h -> h.getNum().equals(holenum))
                        .findFirst();
                List<Player> tempList = new ArrayList<>();
                if(eagledThisHole.size()>0){
                    tempList.addAll(eagledThisHole);
                }else{
                    tempList.addAll(birdiedThisHole);
                }
                if(!thisHole.isPresent() && tempList.size() > 0){
                    Hole newHole = new Hole();
                    newHole.setNum(holenum);
                    newHole.setDate(event.getEventDate());
                    newHole.setIsWinner(Boolean.FALSE);
                    List<String> winners = tempList.stream()
                            .map(p2 -> p2.getName() + ((p2.getEagles().contains(sHole))?" (Eagle) ":""))
                            .collect(Collectors.toList());
                    newHole.getWinners().addAll(winners);
                    coverall.getHoles().add(newHole);
                    newHole.setCoverall(coverall);
                    newHoles.add(newHole);
                }
            }

            //declare winners logic
            if(coverall.getHoles().size() == 18){
                Set<String> winners = new HashSet<>();
                for(Hole h: newHoles){
                    h.setIsWinner(Boolean.TRUE);
                    for(String s : h.getWinners()){
                        winners.add(s.replace(" (Eagle) ",""));
                    }
                }
                Double payout = coverall.getMoney() / winners.size();
                for(String s : winners){
                    StringBuilder builder = new StringBuilder(s);
                    builder.append("   $"+ String.format("%.2f", payout));
                    coverall.getCoverallWinners().add(builder.toString());
                }

                System.out.println(" #winners: "+winners.size()+"   payout: "+payout);
                coverall.setEnddate(LocalDate.now());
                coverall.setStatus(CoverallStatus.COMPLETED);

            }

            coverall.getEvents().add(event);
            event.getCoveralls().add(coverall);
            repository.save(coverall);
        }
        return event;
    }

    @Override
    public List<Coverall> findAll() {
        return repository.findAll().stream()
                .sorted((c1,c2)->c2.getStartdate().compareTo(c1.getStartdate()))
                .collect(Collectors.toList());
    }

    @Override
    public Coverall add(Coverall entity) {
        return repository.save(entity);
    }

    @Override
    public Coverall findById(Long id) {
        return repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Coverall","id",id));
    }

    @Override
    public Coverall update(Long id, Coverall entity) {
        Coverall coverallToEdit = findById(id);
        BeanUtils.copyProperties(entity, coverallToEdit);
        return repository.save(coverallToEdit);
    }

    @Override
    public Coverall deleteById(Long id) {
        Coverall coverallToDelete = findById(id);
        repository.deleteById(id);
        return coverallToDelete;
    }

    @Override
    public Coverall save(Coverall entity) {
        return repository.save(entity);
    }

    @Override
    public CoverallDto toDto(Coverall entity) {
        CoverallDto coverallDto = new CoverallDto();
        BeanUtils.copyProperties(entity, coverallDto);
        return coverallDto;
    }

    @Override
    public Coverall toEntity(CoverallDto dto) {
        Coverall coverall = new Coverall();
        BeanUtils.copyProperties(dto, coverall);
        return coverall;
    }
}
