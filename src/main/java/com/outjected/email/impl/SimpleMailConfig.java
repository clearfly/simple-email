/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.outjected.email.impl;

import java.io.Serializable;

import com.outjected.email.api.SessionConfig;

/**
 * Bean which holds Mail Session configuration options.
 * 
 * @author Cody Lerum
 */
public class SimpleMailConfig implements Serializable, SessionConfig {

    private String serverHost = "localhost";
    private Integer serverPort = 25;
    private String domainName;
    private String username;
    private String password;
    private Boolean enableTls = false;
    private Boolean requireTls = false;
    private Boolean enableSsl = false;
    private Boolean auth = false;
    private String jndiSessionName;

    @Override
    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    @Override
    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Boolean getEnableTls() {
        return enableTls;
    }

    public void setEnableTls(Boolean enableTls) {
        this.enableTls = enableTls;
    }

    @Override
    public Boolean getRequireTls() {
        return requireTls;
    }

    public void setRequireTls(Boolean requireTls) {
        this.requireTls = requireTls;
    }

    @Override
    public Boolean getEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(Boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    @Override
    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    @Override
    public String getJndiSessionName() {
        return jndiSessionName;
    }

    public void setJndiSessionName(String jndiSessionName) {
        this.jndiSessionName = jndiSessionName;
    }

    public boolean isValid() {

        if (jndiSessionName != null && !jndiSessionName.trim().isEmpty()) {
            return true;
        }

        if (serverHost == null || serverHost.trim().isEmpty()) {
            return false;
        }

        return serverPort != 0;
    }

}
