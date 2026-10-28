# CodeAlpha_HotelReservationSystem

Java internship **Task 4** at CodeAlpha: a console Hotel Reservation System.

## Features
- 10 rooms in 3 categories: Standard (Rs.2000), Deluxe (Rs.3500), Suite (Rs.6000) per night
- Search available rooms by date range and category
- Book a room (overlapping dates for the same room are rejected; check-out day is free)
- Payment simulation (Card / UPI / Pay at hotel) with a payment reference number
- View booking details by ID; list all bookings
- Cancel a booking with a refund policy: 100% if 2+ days before check-in, 50% if 1 day, otherwise 0%
- Bookings persist in `bookings.csv` (File I/O), so data survives restarts

## OOP design
`RoomCategory` (enum), `Room`, `Booking`, `Hotel` (business logic + file storage), `Payment`, `HotelReservationSystem` (menu / main).

## Run
```bash
cd src
javac *.java
java HotelReservationSystem
```
Dates use the format `yyyy-MM-dd`, e.g. `2026-11-20`. Requires JDK 8 or newer (uses `java.time`).
Payment is simulated only; no real card data is collected or stored.
