/*
 * Copyright (C) 2022 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.knaw.dans.verifydataset;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DdVerifyDatasetApplication extends Application<DdVerifyDatasetConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DdVerifyDatasetApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dd Verify Dataset";
    }

    @Override
    public void initialize(final Bootstrap<DdVerifyDatasetConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DdVerifyDatasetConfiguration configuration, final Environment environment) {

    }

}
