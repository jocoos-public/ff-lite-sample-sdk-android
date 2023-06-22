package com.jocoos.flipflop.sample.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

interface IOCoroutineScope : CoroutineScope

fun IOCoroutineScope(
    job: Job = SupervisorJob()
): IOCoroutineScope = object : IOCoroutineScope {
    override val coroutineContext = job + Dispatchers.IO
}
