package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId,
                                   Integer parkingLotId,
                                   Integer timeInHours,
                                   Integer numberOfWheels) throws Exception {
        User user;
        ParkingLot parkingLot;
        try {
            user=userRepository3.findById(userId).get();
            parkingLot=parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
        Spot reservedSpot=null;

        int minCost=Integer.MAX_VALUE;

        for(Spot spot: parkingLot.getSpotList()){
            int wheels=0;
            if(spot.getSpotType() == SpotType.TWO_WHEELER)
                wheels=2;
            if(spot.getSpotType() == SpotType.FOUR_WHEELER)
                wheels=4;
            if(spot.getSpotType() == SpotType.OTHERS)
                wheels=24;

            if(!spot.getOccupied() && numberOfWheels <= wheels && spot.getPricePerHour()*timeInHours < minCost) {
                minCost=spot.getPricePerHour()*timeInHours;
                reservedSpot=spot;
            }
        }
        if(reservedSpot == null)
            throw new Exception("Cannot make reservation");

        Reservation reservation=new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setSpot(reservedSpot);

        user.getReservationList().add(reservation);
        reservedSpot.getReservationList().add(reservation);

        reservedSpot.setOccupied(true);

        //reservationRepository3.save(reservation);
        spotRepository3.save(reservedSpot);
        userRepository3.save(user);

        return  reservation;
    }
}