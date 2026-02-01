package com.ifba.clinic.people.messaging.intents.strategies;

import com.ifba.clinic.people.messaging.intents.models.RunProfileIntentMessage;
import com.ifba.clinic.people.messaging.intents.models.responses.ProfileIntentResponse;

public interface ProfileGeneratingStrategy {
  String getProfileType();
  ProfileIntentResponse generateProfile(RunProfileIntentMessage request);
}
