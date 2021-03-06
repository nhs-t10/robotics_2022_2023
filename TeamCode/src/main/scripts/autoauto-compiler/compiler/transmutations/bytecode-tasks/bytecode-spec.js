module.exports = {
    pass: { code: 0x000 },
    jmp_i: { code: 0x001 },
    jmp_l: { code: 0x002 },
    jmp_l_cond: { code: 0x003 },
    jmp_i_cond: { code: 0x004 },
    yieldto_l: { code: 0x005 },
    ret: { code: 0x006 },
    yieldto_i: { code: 0x007 },
    pop: { code: 0x100 },
    dup: { code: 0x101 },
    swap: { code: 0x102 },
    add: { code: 0x200 },
    subtr: { code: 0x201 },
    mul: { code: 0x202 },
    div: { code: 0x203 },
    mod: { code: 0x204 },
    exp: { code: 0x205 },
    cmp_lt: { code: 0x206 },
    cmp_lte: { code: 0x207 },
    cmp_eq: { code: 0x208 },
    cmp_neq: { code: 0x209 },
    cmp_gte: { code: 0x20A },
    cmp_gt: { code: 0x20B },
    abs_dif: { code: 0x20C },
    setvar: { code: 0x300 },
    getvar: { code: 0x301 },
    spec_setvar: { code: 0x302 },
    setprop: { code: 0x303 },
    getprop: { code: 0x304 },
    callfunction: { code: 0x305 },
    makefunction_l: { code: 0x306 },
    unit_currentv: { code: 0x307 },
    construct_table: { code: 0x308 }, 
    construct_relation: { code: 0x309 },
    makefunction_i: { code: 0x30A }
}