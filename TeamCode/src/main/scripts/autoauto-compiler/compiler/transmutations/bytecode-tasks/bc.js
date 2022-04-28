module.exports = {
    0x000: {
        "pop": 0,
        "push": 0,
        "mnemom": "pass"
    },
    0x001: {
        "pop": 1,
        "push": 0,
        "mnemom": "jmp_i"
    },
    0x002: {
        "pop": 1,
        "push": 0,
        "mnemom": "jmp_l"
    },
    0x003: {
        "pop": 2,
        "push": 0,
        "mnemom": "jmp_l_cond"
    },
    0x004: {
        "pop": 2,
        "push": 0,
        "mnemom": "jmp_i_cond"
    },
    0x005: {
        "pop": 1,
        "push": 0,
        "mnemom": "yieldto_l"
    },
    0x006: {
        "pop": 1,
        "push": 0,
        "mnemom": "ret"
    },
    0x007: {
        "pop": 1,
        "push": 0,
        "mnemom": "yieldto_i"
    },
    0x100: {
        "pop": 1,
        "push": 0,
        "mnemom": "pop"
    },
    0x101: {
        "pop": 1,
        "push": 2,
        "mnemom": "dup"
    },
    0x102: {
        "pop": 2,
        "push": 2,
        "mnemom": "swap"
    },
    0x200: {
        "pop": 2,
        "push": 1,
        "mnemom": "add"
    },
    0x201: {
        "pop": 2,
        "push": 1,
        "mnemom": "subtr"
    },
    0x202: {
        "pop": 2,
        "push": 1,
        "mnemom": "mul"
    },
    0x203: {
        "pop": 2,
        "push": 1,
        "mnemom": "div"
    },
    0x204: {
        "pop": 2,
        "push": 1,
        "mnemom": "mod"
    },
    0x205: {
        "pop": 2,
        "push": 1,
        "mnemom": "exp"
    },
    0x206: {
        "pop": 2,
        "push": 1,
        "mnemom": "cmp_lt"
    },
    0x207: {
        "pop": 2,
        "push": 1,
        "mnemom": "cmp_lte"
    },
    0x208: {
        "pop": 2,
        "push": 1,
        "mnemom": "cmp_eq"
    },
    0x209: {
        "pop": 2,
        "push": 1,
        "mnemom": "cmp_neq"
    },
    0x20A: {
        "pop": 2,
        "push": 1,
        "mnemom": "cmp_gte"
    },
    0x20B: {
        "pop": 2,
        "push": 1,
        "mnemom": "cmp_gt"
    },
    0x20C: {
        "pop": 2,
        "push": 1,
        "mnemom": "abs_dif"
    },
    0x300: {
        "pop": 2,
        "push": 0,
        "mnemom": "setvar"
    },
    0x301: {
        "pop": 1,
        "push": 1,
        "mnemom": "getvar"
    },
    0x302: {
        "pop": 2,
        "push": 0,
        "mnemom": "spec_setvar"
    },
    0x303: {
        "pop": 3,
        "push": 0,
        "mnemom": "setprop"
    },
    0x304: {
        "pop": 3,
        "push": 1,
        "mnemom": "getprop"
    },
    0x305: {
        "pop": [undefined, 1],
        "push": 1,
        "mnemom": "callfunction"
    },
    0x306: {
        "pop": [undefined, 1],
        "push": 1,
        "mnemom": "makefunction_l"
    },
    0x307: {
        "pop": 1,
        "push": 1,
        "mnemom": "unit_currentv"
    },
    0x308: {
        "pop": [undefined],
        "push": 1,
        "mnemom": "construct_table"
    },
    0x309: {
        "pop": 2,
        "push": 1,
        "mnemom": "construct_relation"
    },
    0x30A: {
        "pop": [undefined, 1],
        "push": 1,
        "mnemom": "makefunction_i"
    }
}