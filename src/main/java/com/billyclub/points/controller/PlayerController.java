package com.billyclub.points.controller;

import com.billyclub.points.model.Player;
import com.billyclub.points.service.impl.PlayerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/players")
//@CrossOrigin(origins = "http://localhost:8080")
public class PlayerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);
//    @Autowired
    private final PlayerServiceImpl playerService;

//    private final PlayerModelAssembler assembler;
//    @Autowired
//    public PlayerController(PlayerService playerService, PlayerModelAssembler assembler) {
    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
//        this.assembler = assembler;
    }

    //hasRole('ROLE_') hasAnyRole('ROLE_') hasAuthority('permission') hasAnyAuthority('permission')
    @GetMapping
//    public CollectionModel<PlayerModel> getAllPlayers() {
    public List<Player> getAllPlayers() {
//        return assembler.toCollectionModel(playerService.findAll());
        return playerService.findAll();
    }
    @GetMapping("/{id}")
//    public ResponseEntity<PlayerModel> findById(@PathVariable final Long id) {
    public ResponseEntity<Player> findById(@PathVariable final Long id) {
        Player player = playerService.findById(id);
//        PlayerModel playerModel = assembler.toModel(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }
    @PostMapping
//    public ResponseEntity<PlayerModel> addPlayer(@RequestBody Player player) {
    public ResponseEntity<Player> addPlayer(@RequestBody Player player) {
        Player addedPlayer = playerService.add(player);
//        PlayerModel playerModel = assembler.toModel(addedPlayer);
        return new ResponseEntity<>( player, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
//    public ResponseEntity<PlayerModel> updatePlayer(@PathVariable Long id, @RequestBody Player player ) {
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player ) {
        Player addedPlayer = playerService.update(id, player);
//        PlayerModel playerModel = assembler.toModel(addedPlayer);
        return new ResponseEntity<>( player, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
//    public ResponseEntity<PlayerModel> deletePlayer(@PathVariable Long id) {
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id) {
        Player deletedPlayer = playerService.deleteById(id);
//        PlayerModel playerModel = assembler.toModel(deletedPlayer);
        return new ResponseEntity<>(deletedPlayer, HttpStatus.OK);
    }
}
