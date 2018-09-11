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

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.PlexusContainer;

/**
 * Service class to get informations from server
 * 
 * Usage : in your Mojo, declare to properties :
 * <pre>
 *  &#064;Component
 *  private MavenSession session;
 *  
 *  &#064;Component
 *  private PlexusContainer container;
 * </pre>
 * 
 * Then, instanciate a MavenServerUtils, and getServer informations from its id :
 * <pre>
 *    MavenServersUtils serversUtils = new MavenServersUtils(session, container);
 *    ServerInformation serverInfos = serversUtils.getServerAuthentication("serverId");
 * </pre>
 * @author cmarchand
 */
public class MavenServersUtils {

    private final PlexusContainer container;
    private final MavenSession session;
    
    private static final String SECURITY_DISPATCHER_CLASS_NAME =
        "org.sonatype.plexus.components.sec.dispatcher.SecDispatcher";

    /**
     * Creates a new instance
     * @param session MavenSession to use. May be initialized with a <tt>@Component</tt> annotation
     * @param container PlexusContainer to use. May be initialized with a <tt>@Component</tt> annotation.
     */
    public MavenServersUtils(MavenSession session, PlexusContainer container) {
        super();
        this.session=session;
        this.container=container;
    }
    /**
     * Returns authentication of specified server. {@code serverId} is the value defined
     * in settings.xml :
     * <pre>
     * &lt;settings&gt;
     *   &lt;servers&gt;
     *     &lt;server&gt;
     *       &lt;id&gt;serverId&lt;/id&gt;
     *     &lt;/server&gt;
     *   &lt;/servers&gt;
     * &lt;/settings&gt;
     * </pre>
     * @param serverId The server identifier, as defined in settings.xml
     * @return The server informations
     * @throws MojoExecutionException If server does not exists in settings.xml
     */
    public ServerInformation getServerAuthentication(
            final String serverId) throws MojoExecutionException {
        String password;
        if(serverId==null || serverId.isEmpty()) {
            throw new MojoExecutionException("server parameter is required");
        }
        Server settingsServer = session.getSettings().getServer(serverId);
        if(settingsServer==null) {
            throw new MojoExecutionException("Server "+serverId+" is unknown. It should be defined in your settings.xml.");
        }
        password=decryptInlinePasswords(settingsServer.getPassword());
        return new ServerInformation(settingsServer.getUsername(), password);
    }
    
    private String decryptInlinePasswords(String v) {
        Pattern p = Pattern.compile("(\\{[^\\}]+\\})");
        Matcher m = p.matcher(v);
        StringBuffer s = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(s, decryptPassword(m.group(1)));
        }
        m.appendTail(s);
        return s.toString();
    }

    private String decryptPassword(String password) {
        try {
            Class<?> securityDispatcherClass = container.getClass().getClassLoader()
                .loadClass(SECURITY_DISPATCHER_CLASS_NAME);
            Object securityDispatcher = container.lookup(SECURITY_DISPATCHER_CLASS_NAME, "maven");
            Method decrypt = securityDispatcherClass.getMethod("decrypt", String.class);
            return ((String) decrypt.invoke(securityDispatcher, password)).replace("$", "\\$");
        } catch (Exception ignore) {
        }
        return password;
    }
    
}
