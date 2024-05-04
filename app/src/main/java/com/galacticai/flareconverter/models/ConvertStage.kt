package com.galacticai.flareconverter.models

enum class ConvertStage {
    /** initializing on launch */
    Init,

    /** initialization failed */
    InitFail,

    /** user is selecting options */
    Config,

    /** converting file */
    Converting,

    /** convert failed */
    ConvertFail,

    /** sharing file */
    Sharing,
}