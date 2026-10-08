package xyz.devvydont.smprpg.util.chat

enum class CharRepo(val ch: Char, val width: Int, val effectiveWidth: Int = width) {
    A('A', 5),
    a('a', 5),
    B('B', 5),
    b('b', 5),
    C('C', 5),
    c('c', 5),
    D('D', 5),
    d('d', 5),
    E('E', 5),
    e('e', 5),
    F('F', 5),
    f('f', 4),
    G('G', 5),
    g('g', 5),
    H('H', 5),
    h('h', 5),
    I('I', 3),
    i('i', 1),
    J('J', 5),
    j('j', 5),
    K('K', 5),
    k('k', 4),
    L('L', 5),
    l('l', 1),
    M('M', 5),
    m('m', 5),
    N('N', 5),
    n('n', 5),
    O('O', 5),
    o('o', 5),
    P('P', 5),
    p('p', 5),
    Q('Q', 5),
    q('q', 5),
    R('R', 5),
    r('r', 5),
    S('S', 5),
    s('s', 5),
    T('T', 5),
    t('t', 4),
    U('U', 5),
    u('u', 5),
    V('V', 5),
    v('v', 5),
    W('W', 5),
    w('w', 5),
    X('X', 5),
    x('x', 5),
    Y('Y', 5),
    y('y', 5),
    Z('Z', 5),
    z('z', 5),
    NUM_1('1', 5),
    NUM_2('2', 5),
    NUM_3('3', 5),
    NUM_4('4', 5),
    NUM_5('5', 5),
    NUM_6('6', 5),
    NUM_7('7', 5),
    NUM_8('8', 5),
    NUM_9('9', 5),
    NUM_0('0', 5),
    EXCLAMATION_POINT('!', 1),
    AT_SYMBOL('@', 6),
    NUM_SIGN('#', 5),
    DOLLAR_SIGN('$', 5),
    PERCENT('%', 5),
    UP_ARROW('^', 5),
    AMPERSAND('&', 5),
    ASTERISK('*', 5),
    LEFT_PARENTHESIS('(', 4),
    RIGHT_PERENTHESIS(')', 4),
    MINUS('-', 5),
    UNDERSCORE('_', 5),
    PLUS_SIGN('+', 5),
    EQUALS_SIGN('=', 5),
    LEFT_CURL_BRACE('{', 4),
    RIGHT_CURL_BRACE('}', 4),
    LEFT_BRACKET('[', 3),
    RIGHT_BRACKET(']', 3),
    COLON(':', 1),
    SEMI_COLON(';', 1),
    DOUBLE_QUOTE('"', 3),
    SINGLE_QUOTE('\'', 1),
    LEFT_ARROW('<', 4),
    RIGHT_ARROW('>', 4),
    QUESTION_MARK('?', 5),
    SLASH('/', 5),
    BACK_SLASH('\\', 5),
    LINE('|', 1),
    TILDE('~', 5),
    TICK('`', 2),
    PERIOD('.', 1),
    COMMA(',', 1),
    SPACE(' ', 3),
    DEFAULT('\u0000', 0),

    //spacing

    NEG1('\uF801', -1, -2),
    NEG2('\uF802', -2, -3),
    NEG4('\uF804', -4, -5),
    NEG8('\uF808', -8, -9),
    NEG16('\uF809', -16, -17),
    NEG32('\uF80A', -32, -33),
    NEG64('\uF80B', -64, -65),
    NEG128('\uF80C', -128, -129),
    NEG256('\uF80D', -256, -257),
    NEG512('\uF80E', -512, -513),
    NEG1024('\uF80F', -1024, -1025),

    POS1('\uF821', 1, 0),
    POS2('\uF822', 2, 1),
    POS4('\uF824', 4, 3),
    POS8('\uF828', 8, 7),
    POS16('\uF829', 16, 15),
    POS32('\uF82A', 32, 31),
    POS64('\uF82B', 64, 63),
    POS128('\uF82C', 128, 127),
    POS256('\uF82D', 256, 255),
    POS512('\uF82E', 512, 511),
    POS1024('\uF82F', 1024, 1023);

    fun getBoldLength(): Int {
        if (this == SPACE) return this.width
        return this.width + 1
    }

    companion object {
        fun getDefaultFontInfo(c: Char): CharRepo {
            for (dFI in entries.toTypedArray()) {
                if (dFI.ch == c) return dFI
            }
            return DEFAULT
        }

        fun getPixelWidth(str: String): Int {
            var width = 0
            for (ch in str.toCharArray()) {
                width += CharRepo.getDefaultFontInfo(ch).effectiveWidth
            }
            return width
        }
    }
}