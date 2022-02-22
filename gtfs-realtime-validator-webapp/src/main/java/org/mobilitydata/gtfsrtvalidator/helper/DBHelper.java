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

package org.mobilitydata.gtfsrtvalidator.helper;

import org.mobilitydata.gtfsrtvalidator.db.GTFSDB;
import org.mobilitydata.gtfsrtvalidator.lib.model.OccurrenceModel;
import org.mobilitydata.gtfsrtvalidator.lib.model.helper.ErrorListHelperModel;
import org.hibernate.Session;

public class DBHelper {
    public static void saveError(ErrorListHelperModel errorListHelperModel) {
        Session session = GTFSDB.initSessionBeginTrans();
        session.save(errorListHelperModel.getErrorMessage());
        GTFSDB.commitAndCloseSession(session);

        session = GTFSDB.initSessionBeginTrans();
        for (OccurrenceModel occurrence : errorListHelperModel.getOccurrenceList()) {
            occurrence.setMessageLogModel(errorListHelperModel.getErrorMessage());
            session.save(occurrence);
        }
        GTFSDB.commitAndCloseSession(session);
    }
}
