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
 * @author Cody Lerum
 */
public enum MessagePriority {
    LOW("5", "non-urgent", "low"),
    NORMAL("3", "normal", "normal"),
    HIGH("1", "urgent", "high");

    private String xPriority;
    private String priority;
    private String importance;

    private MessagePriority(String x_priority, String priority, String importance) {
        this.xPriority = x_priority;
        this.priority = priority;
        this.importance = importance;
    }

    public String getX_priority() {
        return xPriority;
    }

    public String getPriority() {
        return priority;
    }

    public String getImportance() {
        return importance;
    }
}
