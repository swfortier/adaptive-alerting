/*
 * Copyright 2018 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.anomdetect;

import com.opencsv.bean.CsvBindByName;

public class PewmaTestRow {
    
    @CsvBindByName
    private double observed;
    
    @CsvBindByName
    private double mean;

    @CsvBindByName
    private String level;
    
    @CsvBindByName
    private double std;

    public double getObserved() {
        return observed;
    }

    public void setObserved(double observed) {
        this.observed = observed;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }
}