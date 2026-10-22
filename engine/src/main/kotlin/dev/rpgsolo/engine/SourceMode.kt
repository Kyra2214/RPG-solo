package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/** DC-05.2: como a campanha trata o documento-fonte. Persistido no log da campanha (evento [SourceModeSet]). */
@Serializable
enum class SourceMode { FAITHFUL, ALTERNATE, INSPIRED }
