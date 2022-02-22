/*
 * Copyright (C) 2011 Nipuna Gunathilake.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsrtvalidator.lib.validation.gtfs;

import org.mobilitydata.gtfsrtvalidator.lib.model.MessageLogModel;
import org.mobilitydata.gtfsrtvalidator.lib.model.OccurrenceModel;
import org.mobilitydata.gtfsrtvalidator.lib.model.helper.ErrorListHelperModel;
import org.mobilitydata.gtfsrtvalidator.lib.util.RuleUtils;
import org.mobilitydata.gtfsrtvalidator.lib.validation.interfaces.GtfsFeedValidator;
import org.mobilitydata.gtfsrtvalidator.lib.validation.ValidationRules;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * E010 - If location_type is used in stops.txt, all stops referenced in stop_times.txt must have location_type of 0
 */
public class StopLocationTypeValidator implements GtfsFeedValidator {

    private static final org.slf4j.Logger _log = LoggerFactory.getLogger(StopLocationTypeValidator.class);

    @Override
    public List<ErrorListHelperModel> validate(GtfsDaoImpl gtfsData) {
        List<OccurrenceModel> e010List = new ArrayList<>();

        Collection<StopTime> stopTimes = gtfsData.getAllStopTimes();
        Set<Stop> checkedStops = new HashSet<>();

        for (StopTime stopTime : stopTimes) {
            if (!checkedStops.contains(stopTime.getStop())) {
                checkedStops.add(stopTime.getStop());

                if (stopTime.getStop().getLocationType() != 0) {
                    RuleUtils.addOccurrence(ValidationRules.E010, "stop_id " + stopTime.getStop().getId(), e010List, _log);
                }
            }
        }

        List<ErrorListHelperModel> errors = new ArrayList<>();
        if (!e010List.isEmpty()) {
            errors.add(new ErrorListHelperModel(new MessageLogModel(ValidationRules.E010), e010List));
        }
        return errors;
    }
}
