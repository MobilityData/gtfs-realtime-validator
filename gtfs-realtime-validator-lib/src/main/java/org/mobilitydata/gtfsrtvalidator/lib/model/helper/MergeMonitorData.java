/*
 * Copyright (C) 2017 University of South Florida.
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
package org.mobilitydata.gtfsrtvalidator.lib.model.helper;

import org.mobilitydata.gtfsrtvalidator.lib.model.ViewErrorLogModel;
import org.mobilitydata.gtfsrtvalidator.lib.model.ViewErrorSummaryModel;
import org.mobilitydata.gtfsrtvalidator.lib.model.ViewGtfsRtFeedErrorCountModel;

import java.util.ArrayList;
import java.util.List;

public class MergeMonitorData {

    private int iterationCount;
    private int uniqueFeedCount;
    private List<ViewGtfsRtFeedErrorCountModel> viewGtfsRtFeedErrorCountModelList;
    private List<ViewErrorSummaryModel> viewErrorSummaryModelList;
    private List<ViewErrorLogModel> viewErrorLogModelList;

    public MergeMonitorData() {

        viewGtfsRtFeedErrorCountModelList = new ArrayList<>();
        viewErrorSummaryModelList = new ArrayList<>();
        viewErrorLogModelList = new ArrayList<>();
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public int getUniqueFeedCount() {
        return uniqueFeedCount;
    }

    public void setUniqueFeedCount(int uniqueFeedCount) {
        this.uniqueFeedCount = uniqueFeedCount;
    }

    public List<ViewGtfsRtFeedErrorCountModel> getViewGtfsRtFeedErrorCountModelList() {
        return viewGtfsRtFeedErrorCountModelList;
    }

    public void setViewGtfsRtFeedErrorCountModelList(List<ViewGtfsRtFeedErrorCountModel> viewGtfsRtFeedErrorCountModelList) {
        this.viewGtfsRtFeedErrorCountModelList = viewGtfsRtFeedErrorCountModelList;
    }

    public List<ViewErrorSummaryModel> getViewErrorSummaryModelList() {
        return viewErrorSummaryModelList;
    }

    public void setViewErrorSummaryModelList(List<ViewErrorSummaryModel> viewErrorSummaryModelList) {
        this.viewErrorSummaryModelList = viewErrorSummaryModelList;
    }

    public List<ViewErrorLogModel> getViewErrorLogModelList() {
        return viewErrorLogModelList;
    }

    public void setViewErrorLogModelList(List<ViewErrorLogModel> viewErrorLogModelList) {
        this.viewErrorLogModelList = viewErrorLogModelList;
    }
}
