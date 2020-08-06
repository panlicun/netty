package com.topwalk.core.attribute;

import com.topwalk.core.session.Session;
import io.netty.util.AttributeKey;

public interface Attributes {

	AttributeKey<Session> SESSION = AttributeKey.newInstance("session");

}
