package com.github.alikemalocalan.model

import java.time.OffsetDateTime
import java.util.UUID

case class RememberMeToken(id: UUID, selector: String, tokenHash: String, userId: UUID, validTo: OffsetDateTime)
