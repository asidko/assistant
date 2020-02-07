package com.space.assistant.service.error

import com.space.assistant.core.entity.ActiveJobInfo

class ActiveJobNotFoundException(activeJobInfo: ActiveJobInfo) : Exception("Active job not found using info $activeJobInfo") {
}
