package dev.duque.api.parkingcontrol.controllers;

import dev.duque.api.parkingcontrol.dtos.ParkingSpotDTO;
import dev.duque.api.parkingcontrol.model.ParkingSpotModel;
import dev.duque.api.parkingcontrol.services.ParkingSpotService;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Api(value = "ParkingSpotController")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/parking-spot")
public class ParkingSpotController {


    final ParkingSpotService parkingSpotService;
        public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO){

        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())){
            return new ResponseEntity<>("Car already registered", HttpStatus.CONFLICT);
        }

        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Parking spot already in use by this apartment and block");
        }

        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Parking spot already in use");
        }

        ParkingSpotModel parkingSpotModel = new ParkingSpotModel(); //Poderia ser var parkingSpotModel = new ParkingSpotModel();

        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);  //Converte o objeto parkingSpotDTO para o objeto parkingSpotModel

        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC"))); //Seta a data de registro do veículo. Falta a ISO-8601 e Offset.

        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));

    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){

        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);

        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id, @RequestBody @Valid ParkingSpotDTO parkingSpotDTO){

        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);

        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
        }


//        var parkingSpotModel = parkingSpotModelOptional.get();
//        parkingSpotModel.setParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber());
//        parkingSpotModel.setApartment(parkingSpotDTO.getApartment());
//        parkingSpotModel.setBlock(parkingSpotDTO.getBlock());
//        ETC...


        ParkingSpotModel parkingSpotModel = new ParkingSpotModel();

        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);

        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){

        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);

        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
        }

        parkingSpotService.delete(parkingSpotModelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body("Parking spot deleted");

    }

}
