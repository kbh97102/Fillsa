package com.arakene.domain.util

class CommonErrorWrappedException(val commonError: CommonError) : Exception(commonError.toString())