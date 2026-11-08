package dev.rpgsolo.world

import java.math.BigInteger

/** Réplica bit a bit de `random.Random(int)` do CPython 3.12 (MT19937): `random`, `getrandbits`, `randrange`, `randint`, `choice`. */
class PythonRandom(semente: BigInteger) {
    private val mt = IntArray(N)
    private var indice = N

    constructor(semente: Long) : this(BigInteger.valueOf(semente))

    init {
        val chave = chaveDe(semente.abs())
        iniciar(19650218)
        var i = 1
        var j = 0
        var k = maxOf(N, chave.size)
        while (k > 0) {
            val p = mt[i - 1]
            mt[i] = (mt[i] xor ((p xor (p ushr 30)) * 1664525)) + chave[j] + j
            i++
            j++
            if (i >= N) {
                mt[0] = mt[N - 1]
                i = 1
            }
            if (j >= chave.size) j = 0
            k--
        }
        k = N - 1
        while (k > 0) {
            val p = mt[i - 1]
            mt[i] = (mt[i] xor ((p xor (p ushr 30)) * 1566083941)) - i
            i++
            if (i >= N) {
                mt[0] = mt[N - 1]
                i = 1
            }
            k--
        }
        mt[0] = Int.MIN_VALUE
        indice = N
    }

    private fun iniciar(s: Int) {
        mt[0] = s
        for (i in 1 until N) {
            val p = mt[i - 1]
            mt[i] = 1812433253 * (p xor (p ushr 30)) + i
        }
    }

    private fun chaveDe(n: BigInteger): IntArray {
        val palavras = if (n.signum() == 0) 1 else (n.bitLength() - 1) / 32 + 1
        val mascara = BigInteger.valueOf(0xFFFFFFFFL)
        return IntArray(palavras) { n.shiftRight(32 * it).and(mascara).toLong().toInt() }
    }

    private fun gerar() {
        for (kk in 0 until N) {
            val y = (mt[kk] and UPPER) or (mt[(kk + 1) % N] and LOWER)
            var v = mt[(kk + M) % N] xor (y ushr 1)
            if (y and 1 != 0) v = v xor MATRIZ
            mt[kk] = v
        }
        indice = 0
    }

    private fun palavra(): Int {
        if (indice >= N) gerar()
        var y = mt[indice++]
        y = y xor (y ushr 11)
        y = y xor ((y shl 7) and 0x9d2c5680.toInt())
        y = y xor ((y shl 15) and 0xefc60000.toInt())
        y = y xor (y ushr 18)
        return y
    }

    /** `random.random()`. */
    fun random(): Double {
        val a = (palavra() ushr 5).toLong()
        val b = (palavra() ushr 6).toLong()
        return (a * 67108864.0 + b) * (1.0 / 9007199254740992.0)
    }

    /** `getrandbits(k)` para 0 ≤ k ≤ 62. */
    fun getrandbits(k: Int): Long {
        require(k in 0..62) { "k fora de 0..62: $k" }
        if (k == 0) return 0L
        var restante = k
        var r = 0L
        var deslocamento = 0
        while (restante > 0) {
            var w = palavra().toLong() and 0xFFFFFFFFL
            if (restante < 32) w = w ushr (32 - restante)
            r = r or (w shl deslocamento)
            deslocamento += 32
            restante -= 32
        }
        return r
    }

    private fun randbelow(n: Long): Long {
        if (n == 0L) return 0L
        val k = 64 - java.lang.Long.numberOfLeadingZeros(n)
        var r = getrandbits(k)
        while (r >= n) r = getrandbits(k)
        return r
    }

    /** `randrange(stop)`. */
    fun randrange(stop: Int): Int {
        require(stop > 0) { "empty range for randrange()" }
        return randbelow(stop.toLong()).toInt()
    }

    /** `randrange(start, stop)`. */
    fun randrange(start: Int, stop: Int): Int {
        require(stop > start) { "empty range in randrange($start, $stop)" }
        return start + randbelow((stop - start).toLong()).toInt()
    }

    /** `randint(a, b)`, com `b` incluído. */
    fun randint(a: Int, b: Int): Int = randrange(a, b + 1)

    /** `choice(seq)`. */
    fun <T> choice(seq: List<T>): T {
        require(seq.isNotEmpty()) { "Cannot choose from an empty sequence" }
        return seq[randbelow(seq.size.toLong()).toInt()]
    }

    companion object {
        private const val N = 624
        private const val M = 397
        private const val MATRIZ = 0x9908b0df.toInt()
        private const val UPPER = 0x80000000.toInt()
        private const val LOWER = 0x7fffffff

        /** Semente do simulador: `int(sha256("semente|motor|dia")[:16], 16)`. */
        fun sementeDoDia(semente: String, motor: String, dia: Int): BigInteger =
            BigInteger(Hash.sha256("$semente|$motor|$dia").substring(0, 16), 16)

        fun doDia(semente: String, motor: String, dia: Int): PythonRandom = PythonRandom(sementeDoDia(semente, motor, dia))
    }
}
