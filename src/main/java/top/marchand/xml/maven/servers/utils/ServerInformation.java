/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package top.marchand.xml.maven.servers.utils;

/**
 * This class holds authentication informations
 * @author cmarchand
 */
public class ServerInformation {
    private final String username;
    private final String password;
    public ServerInformation(final String username, final String password) {
        super();
        this.username=username;
        this.password=password;
    }
    /**
     * The username specified in server definition
     * @return 
     */
    public String getUsername() { return username; }
    /**
     * The password specified in server definition. If password was crypted,
     * return value is decrypted.
     * @return 
     */
    public String getPassword() { return password; }
}