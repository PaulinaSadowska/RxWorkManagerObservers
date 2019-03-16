package com.paulinasadowska.rxworkmanagerobservers.exceptions

import java.lang.IllegalStateException

class LiveDataSubscribedOnWrongThreadException
    : IllegalStateException("cannot subscribe to LiveData on the background thread")