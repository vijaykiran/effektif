/* Copyright (c) 2014, Effektif GmbH.
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
 * limitations under the License. */
package com.effektif.workflow.slack.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Tom Baeyens
 */
public class SlackService {

    private static final Logger log = LoggerFactory.getLogger(SlackService.class);

    Map<String, SlackAccount> slackAccounts = new HashMap<>();

    public SlackAccount findAccount(String slackAccountId) {
        return slackAccounts.get(slackAccountId);
    }

    public void addAccount(SlackAccount slackAccount) {
        slackAccounts.put(slackAccount.getId(), slackAccount);
        slackAccount.slackService = this;
    }

    public void createPost(String username, String password, String channel, String message) {
        log.debug("creating post on channel '" + channel + "': " + message);
    }
}
