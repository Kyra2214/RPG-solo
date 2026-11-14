package dev.rpgsolo.ui

import kotlin.random.Random

/**
 * Sorteio de nomes da criação por abas (CG-4.5). Kotlin puro, sem Android.
 * Segue o molde de `Mythralis/mundo/M-10-nomes-minimo.md`: nome curto de 1 a 3 sílabas, com epíteto opcional.
 * O tom vem do reino de origem; raça não determina o nome (regra 4 do tom, W-01).
 * ASSUMINDO: as sílabas e os epítetos abaixo são conteúdo próprio do app (o cânone não traz lista de nomes);
 * os primeiros nomes reservados dos reinos 1 a 3 nunca saem do sorteio.
 */
object SorteioNomes {
    private val reservados: Set<String> =
        (
            "Alena Anelra Arieth Asha Avariel Avarra Bento Beren Calar Dalen Dara Dauren Dena Doria Edris Elarwen Elsin Elyon " +
                "Eryndra Eshara Fen Hadrik Ilyra Iria Ivara Iven Kheva Korga Lethariel Luma Lyris Maelor Maereth Mara Marel Meda " +
                "Milo Mira Mirek Mirel Miri Nara Neralis Nessa Nilo Nima Nimeth Nivara Náren Oranna Orelia Oren Oru Othalen Parel " +
                "Rovan Ruma Saelith Sava Sela Selenê Selvaris Sena Sivra Syranna Syris Tavian Tavro Teren Terenna Tessa Tharion " +
                "Tirven Toma Tália Vaunor Vela Vessa Veyra Voro Yara " +
                "Aurea Aurel Brakka Brundar Brunhild Ciri Dorn Embra Fenwick Gorrak Gundrik Haldra Hrodgar Ilmeth Iridel Iskra " +
                "Karna Maro Maur Miraq Nael Naru Orla Pelma Pimm Pip Sevro Sharra Skit Thaurak Throm Tik Tobiah Torgrim Tuck " +
                "Varkhan Velka Vireo Ysmira Zeka Zhae " +
                "Morvan Orsk Cerrane Ilkar Tazk Rusk Vurga Teshi Yeshe Krell Ondra Dhun Nuvik Lorra Ssavi Brisk Garrun Szia Rhek " +
                "Ardh Kerrak Ethra Piok Noor Sorren Azeroth"
        ).split(" ").map { it.lowercase() }.toSet()

    private class Tom(val inicios: List<String>, val meios: List<String>, val fins: List<String>)

    private val geral =
        Tom(
            listOf("Ba", "Ca", "Da", "Fe", "Ga", "Ha", "Ja", "Ka", "La", "Ma", "Na", "Pa", "Ra", "Sa", "Ta", "Va"),
            listOf("le", "ri", "no", "ve", "do", "mi", "ta", "su"),
            listOf("n", "r", "l", "s", "da", "ro", "ma", "ne"),
        )

    private val tons: Map<String, Tom> =
        mapOf(
            "aeltharyn" to
                Tom(
                    listOf("Ae", "El", "Fa", "Il", "Lu", "Ny", "Sy", "Ta", "Ve", "Yl"),
                    listOf("la", "ri", "ve", "ni", "se", "lo", "ra"),
                    listOf("n", "l", "th", "ne", "ra", "wen", "ris"),
                ),
            "coroas-de-pedra" to
                Tom(
                    listOf("Bro", "Dar", "Gru", "Hor", "Kor", "Mok", "Tro", "Vor", "Zag", "Dur"),
                    listOf("n", "k", "ra", "du", "gar", "ul"),
                    listOf("k", "m", "rk", "n", "dak", "lin", "gar"),
                ),
            "veu-dos-malditos" to
                Tom(
                    listOf("Ss", "Vel", "Mor", "Ny", "Qui", "Ul", "Ze", "Ish", "Oth", "Sul"),
                    listOf("sa", "ze", "ro", "ka", "vi", "u"),
                    listOf("th", "sk", "ss", "va", "ek", "or", "ia"),
                ),
            "vale-palancar" to
                Tom(
                    listOf("Bar", "Ger", "Her", "Lor", "Pen", "Ren", "Tam", "Val", "Wen", "Bel"),
                    listOf("a", "e", "o", "ti", "re"),
                    listOf("mo", "ro", "do", "lo", "ta", "ne", "ben"),
                ),
        )

    private val epitetoBase =
        listOf(
            "Arco", "Fio", "Raiz", "Vento", "Pedra", "Brasa", "Lume", "Maré", "Cinza", "Trigo", "Sombra", "Ferro", "Pena", "Musgo",
        )
    private val epitetoComplemento =
        listOf("Longa", "Baixa", "Viva", "Funda", "Mansa", "Clara", "Velha", "Alta", "Cega", "Firme")
    private val epitetoNumero = listOf("Três", "Sete", "Quatro", "Cinco", "Nove", "Doze")
    private val epitetoSubstantivo = listOf("Pontes", "Portas", "Correntes", "Colinas", "Luzes", "Raízes", "Estações", "Velas")

    private fun tom(reinoId: String?): Tom {
        val chave = reinoId.orEmpty().substringAfter("kingdom.")
        return tons[chave] ?: geral
    }

    private fun primeiroNome(
        tom: Tom,
        aleatorio: Random,
    ): String {
        val silabas = aleatorio.nextInt(1, 4)
        val texto =
            when (silabas) {
                1 -> tom.inicios.random(aleatorio) + tom.fins.random(aleatorio)
                2 -> tom.inicios.random(aleatorio) + tom.meios.random(aleatorio) + tom.fins.random(aleatorio)
                else -> tom.inicios.random(aleatorio) + tom.meios.random(aleatorio) + tom.meios.random(aleatorio) + tom.fins.random(aleatorio)
            }
        return texto.lowercase().replaceFirstChar { it.uppercaseChar() }
    }

    private fun epiteto(aleatorio: Random): String =
        when (aleatorio.nextInt(3)) {
            0 -> epitetoBase.random(aleatorio) + "-" + epitetoComplemento.random(aleatorio)
            1 -> epitetoNumero.random(aleatorio) + "-" + epitetoSubstantivo.random(aleatorio)
            else -> "das " + epitetoNumero.random(aleatorio) + " " + epitetoSubstantivo.random(aleatorio)
        }

    /** Primeiro nome reservado do cânone de Mythralis (comparação sem diferenciar maiúsculas). */
    fun reservado(nome: String): Boolean = nome.trim().substringBefore(" ").lowercase() in reservados

    /**
     * Sorteia um nome para o herói do reino `reinoId` (ex.: `kingdom.aeltharyn`; nulo ou desconhecido usa o tom geral).
     * Cerca de metade dos nomes leva epíteto. Nunca devolve um primeiro nome reservado nem igual a `evitar`.
     */
    fun sortear(
        reinoId: String?,
        aleatorio: Random = Random.Default,
        evitar: String = "",
    ): String {
        val tom = tom(reinoId)
        repeat(50) {
            val primeiro = primeiroNome(tom, aleatorio)
            if (reservado(primeiro)) return@repeat
            val nome = if (aleatorio.nextBoolean()) primeiro + " " + epiteto(aleatorio) else primeiro
            if (!nome.equals(evitar.trim(), ignoreCase = true)) return nome
        }
        return "Sem-Nome"
    }
}
