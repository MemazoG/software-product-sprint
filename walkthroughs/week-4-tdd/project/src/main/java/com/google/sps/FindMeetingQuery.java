// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

//PROCESS: Iterate over the events given checking if there are attendees
//of the meeting request. If there are, put the time (start-end) of the event in a list.
//At the end, search the empty spaces in the list and add the possible
//options to the 'options' list

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //List I'll be returning
    List<TimeRange> options = new ArrayList();
    //List where I'll store occupied time slots
    List<TimeRange> occupied = new ArrayList();

    //SPECIAL CASE: Duration longer than the day = Return empty list
    if(request.getDuration() > TimeRange.END_OF_DAY){
        return options;
    }

    //SPECIAL CASES: No attendees and/or no events = Whole day available
    if(request.getAttendees().isEmpty() || events.isEmpty()){
        options.add(TimeRange.WHOLE_DAY);
        return options;
    }

    //Checks if the given events contain at least one of the meeting's attendees
    for(Event event : events) {
        if(containsAttendee(event, request.getAttendees())){
            occupied.add(event.getWhen());
        }
    }

    //SPECIAL CASE: If occupied is empty at this point, the meeting can be at any time of the day, so return the whole day available
    if(occupied.isEmpty()){
        options.add(TimeRange.WHOLE_DAY);
        return options;
    }

    //Sorting the occupied time slots by starting time from earliest to latest
    Comparator<TimeRange> comp = new SortByStartTime();
    Collections.sort(occupied, comp);

    // |---------|
    //      |-------|
    // |------------|

    //Handling overlapping/nested time slots
    for(int i=0; i<occupied.size()-1; i++){
        //If 2 time slots overlap, replace it with one time slot that covers both
        if(occupied.get(i).overlaps(occupied.get(i+1))){
            if(occupied.get(i+1).end() > occupied.get(i).end()){
                //New duration = 1st time slot + 2nd time slot - the part where they overlap
                occupied.set(i, TimeRange.fromStartDuration(occupied.get(i).start(), occupied.get(i).duration() + occupied.get(i+1).duration() - (occupied.get(i).end() - occupied.get(i+1).start())));
                occupied.remove(i+1);
            } else {
                //This means the 2nd event is completely inside the 1st one (nested)
                occupied.remove(i+1);
            }
        }
    }

    //Checks the duration from the start of the day to the start of the 1st time slot
    if(occupied.get(0).start() >= request.getDuration()){
        options.add(TimeRange.fromStartEnd(0, occupied.get(0).start(), false));
    }
    //Checks the duration between each time slot
    for(int i=0; i<occupied.size()-1; i++){
        if(occupied.get(i+1).start() - occupied.get(i).end() >= request.getDuration()){
            options.add(TimeRange.fromStartDuration(occupied.get(i).end(), occupied.get(i+1).start() - occupied.get(i).end()));
        }
    }
    //Checks the duration from the end of the last time slot to the end of the day
    if(TimeRange.END_OF_DAY - occupied.get(occupied.size()-1).end() >= request.getDuration()){
        options.add(TimeRange.fromStartEnd(occupied.get(occupied.size()-1).end(), TimeRange.END_OF_DAY, true));
    }

    return options;
  }

  public boolean containsAttendee(Event event, Collection<String> attendees){
      //Loops through the meeting's attendees and checks if they're in the event
      for(String attendee : attendees){
          if(event.getAttendees().contains(attendee)){
              return true;
          }
      }
      return false;
  }
}
