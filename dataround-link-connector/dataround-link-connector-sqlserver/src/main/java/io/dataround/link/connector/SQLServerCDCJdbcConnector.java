/*
 * Copyright 2025 yuehan124@gmail.com
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

package io.dataround.link.connector;

import io.dataround.link.common.utils.ConnectorNameConstants;

/**
 * SQLServer CDC connector
 * 
 * @author yuehan124@gmail.com
 * @since 2025-08-04
 */
public class SQLServerCDCJdbcConnector extends SQLServerJdbcConnector {

    private final String name = ConnectorNameConstants.SQLSERVER_CDC;

    @Override
    public String getName() {
        return this.name;
    }
}
