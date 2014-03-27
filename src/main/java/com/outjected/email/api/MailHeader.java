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

package com.outjected.email.api;

/**
 * Defines the available message receipt headers
 * 
 * @author Cody Lerum
 */
public enum MailHeader
{
    DELIVERY_RECIEPT("Return-Receipt-To"),
    READ_RECIEPT("Disposition-Notification-To");

    private String headerValue;

    private MailHeader(String headerValue)
    {
        this.headerValue = headerValue;
    }

    public String headerValue()
    {
        return headerValue;
    }
}
