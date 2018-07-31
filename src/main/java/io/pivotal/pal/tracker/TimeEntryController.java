package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
    private TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {

        this.timeEntryRepository = timeEntryRepository;
    }

    @RequestMapping(value = "/time-entries", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
       TimeEntry timeEntry = timeEntryRepository.create(timeEntryToCreate);

       return  ResponseEntity.status(HttpStatus.CREATED).body(timeEntry);
    }

    @RequestMapping(value = "/time-entries/{id}", method = RequestMethod.GET)
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long l) {
        TimeEntry timeEntry = timeEntryRepository.find(l);
        if( timeEntry == null)
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(timeEntry);
        else
            return  ResponseEntity.status(HttpStatus.OK).body(timeEntry);

    }

    @RequestMapping(value = "/time-entries", method = RequestMethod.GET)
    public ResponseEntity<List<TimeEntry>> list() {

        List<TimeEntry> listTimeEntries = timeEntryRepository.list();

        return ResponseEntity.status(HttpStatus.OK).body(listTimeEntries);
    }

    @RequestMapping(value = "/time-entries/{id}", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable("id") long l, @RequestBody TimeEntry expected) {
        TimeEntry updatedTimeEntry = timeEntryRepository.update(l, expected);
        if( updatedTimeEntry == null)
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedTimeEntry);
        else
            return ResponseEntity.status(HttpStatus.OK).body(updatedTimeEntry);
    }


    @RequestMapping(value = "/time-entries/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long l) {
        timeEntryRepository.delete(l);

        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
