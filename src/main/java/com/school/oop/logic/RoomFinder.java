package com.school.oop.logic;

import com.school.oop.model.Booking;
import com.school.oop.model.Room;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

public class RoomFinder {
    private final ArrayList<Booking> bookings = createTimetableBookings();

    // Return rooms matching the filters and mark their status for the selected time.
    public ArrayList<RoomResult> searchRooms(
            String building, int minimumCapacity, String roomType, DayOfWeek day, LocalTime time) {
        ArrayList<RoomResult> results = new ArrayList<>();
        for (Room room : createRoomInventory()) {
            boolean matchesBuilding = building == null || building.isBlank()
                    || room.getBuilding().equals(building);
            boolean matchesType = roomType == null || roomType.isBlank()
                    || room.getType().equals(roomType);
            boolean matchesCapacity = room.getCapacity() >= minimumCapacity;

            if (matchesBuilding && matchesType && matchesCapacity) {
                String availability = isOccupied(room.getRoomId(), day, time)
                        ? "Occupied" : "Vacant";
                results.add(new RoomResult(room, availability));
            }
        }
        return results;
    }

    // Add a lecture booking if the selected room is free for the whole time.
    public String bookRoom(
            String roomId, DayOfWeek day, LocalTime startTime, LocalTime endTime, String courseName) {
        if (roomId == null || day == null || startTime == null || endTime == null
                || courseName == null || courseName.isBlank()) {
            return "Enter a course name and valid booking time.";
        }
        if (!endTime.isAfter(startTime)) {
            return "The end time must be after the start time.";
        }
        if (isAlwaysOccupied(roomId) || hasTimeConflict(roomId, day, startTime, endTime)) {
            return "That room is already occupied during the requested time.";
        }

        bookings.add(new Booking(roomId, day, startTime, endTime, courseName.trim()));
        return null;
    }

    private boolean isOccupied(String roomId, DayOfWeek day, LocalTime time) {
        if (isAlwaysOccupied(roomId)) {
            return true;
        }
        for (Booking booking : bookings) {
            if (booking.getRoomId().equals(roomId)
                    && booking.getDay() == day
                    && !time.isBefore(booking.getStartTime())
                    && time.isBefore(booking.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlwaysOccupied(String roomId) {
        
        return "MSB 8".equals(roomId);
    }

    private boolean hasTimeConflict(
            String roomId, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        for (Booking booking : bookings) {
            boolean sameRoomAndDay = booking.getRoomId().equals(roomId)
                    && booking.getDay() == day;
            boolean timesOverlap = startTime.isBefore(booking.getEndTime())
                    && endTime.isAfter(booking.getStartTime());
            if (sameRoomAndDay && timesOverlap) {
                return true;
            }
        }
        return false;
    }

    // Store the legible room/time entries from the supplied weekly timetable.
    private ArrayList<Booking> createTimetableBookings() {
        ArrayList<Booking> timetable = new ArrayList<>();
        timetable.add(new Booking("F5-04", DayOfWeek.MONDAY,
                LocalTime.of(10, 15), LocalTime.of(12, 15), "Integrals Calculus"));
        timetable.add(new Booking("F2-01", DayOfWeek.MONDAY,
                LocalTime.of(12, 15), LocalTime.of(13, 15), "Masha Programs II"));
        timetable.add(new Booking("F2-01", DayOfWeek.MONDAY,
                LocalTime.of(15, 15), LocalTime.of(17, 15), "Communication Skills II"));
        timetable.add(new Booking("Lib 1", DayOfWeek.TUESDAY,
                LocalTime.of(8, 15), LocalTime.of(10, 15), "Computer Networks"));
        timetable.add(new Booking("F5-02", DayOfWeek.TUESDAY,
                LocalTime.of(13, 15), LocalTime.of(15, 15), "Communication Skills II / Linear Algebra"));
        timetable.add(new Booking("F1-05", DayOfWeek.WEDNESDAY,
                LocalTime.of(8, 15), LocalTime.of(10, 15), "Principles of Management"));
        timetable.add(new Booking("F5-01", DayOfWeek.WEDNESDAY,
                LocalTime.of(12, 15), LocalTime.of(14, 15), "Object Oriented Programming I"));
        timetable.add(new Booking("F1-01", DayOfWeek.WEDNESDAY,
                LocalTime.of(15, 15), LocalTime.of(17, 15), "Database Systems"));
        timetable.add(new Booking("MSB 4", DayOfWeek.THURSDAY,
                LocalTime.of(8, 15), LocalTime.of(10, 15), "Database Systems"));
        timetable.add(new Booking("F1-04", DayOfWeek.THURSDAY,
                LocalTime.of(10, 15), LocalTime.of(12, 15), "Linear Algebra"));
        timetable.add(new Booking("Rm 1", DayOfWeek.THURSDAY,
                LocalTime.of(13, 15), LocalTime.of(14, 15), "Masha Programs II"));
        timetable.add(new Booking("MSB 13", DayOfWeek.THURSDAY,
                LocalTime.of(15, 15), LocalTime.of(17, 15), "Integrals Calculus"));
        timetable.add(new Booking("MSB 10", DayOfWeek.FRIDAY,
                LocalTime.of(10, 15), LocalTime.of(12, 15), "Computer Networks"));
        timetable.add(new Booking("MSB 13", DayOfWeek.FRIDAY,
                LocalTime.of(13, 15), LocalTime.of(14, 15), "Principles of Management"));
        return timetable;
    }

    // Building the temporary inventory from the room and capacity details provided.
    private ArrayList<Room> createRoomInventory() {
        ArrayList<Room> rooms = new ArrayList<>();

        addClassrooms(rooms, "Oval Building", "SLS Shaba", "SLS Zumaridi");

        addClassrooms(rooms, "STMB",
                "F1-02", "F1-03", "F1-04", "F1-05",
                "F2-01", "F2-02", "F2-03", "F2-04", "F2-05",
                "F5-01", "F5-02", "F5-03", "F5-04", "F5-05");

        for (int roomNumber = 1; roomNumber <= 14; roomNumber++) {
            addClassrooms(rooms, "MSB", "MSB " + roomNumber);
        }

        addLabs(rooms, "STC", "Chela Lab");
        addClassrooms(rooms, "SBS", "SBS 1", "SBS 2");
        addLabs(rooms, "Central Building", "Masinga Lab", "Suswa Lab");

        for (int roomNumber = 1; roomNumber <= 4; roomNumber++) {
            addClassrooms(rooms, "Central Building", "LT " + roomNumber);
        }
        for (int roomNumber = 1; roomNumber <= 6; roomNumber++) {
            addClassrooms(rooms, "Central Building", "Rm " + roomNumber);
        }
        addClassrooms(rooms, "Library", "Lib 1");
        return rooms;
    }

    private void addClassrooms(ArrayList<Room> rooms, String building, String... roomIds) {
        for (String roomId : roomIds) {
            rooms.add(new Room(roomId, building, 63, "Classroom"));
        }
    }

    private void addLabs(ArrayList<Room> rooms, String building, String... roomIds) {
        for (String roomId : roomIds) {
            rooms.add(new Room(roomId, building, 42, "Lab"));
        }
    }

    public ArrayList<Booking> getBookingsForStratizen(String stratizenId) {
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        bookings.add(new Booking("A101", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 30), "OOP"));
        return bookings;
    }

    public String addBooking(String stratizenId, String roomId, DayOfWeek day, LocalTime startTime, LocalTime endTime, String courseName) {
        return "OK";
    }

    public String cancelBooking(String stratizenId, String bookingId) {
        return "OK";
    }
}
