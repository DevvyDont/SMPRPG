package xyz.devvydont.smprpg.util.chat

class UnicodeSpaceUtil {

    companion object {
        fun getSpace(spacing: Int): String {
            val isNeg = spacing < 0
            var retString = ""
            var currSpacing = spacing
            while (currSpacing != 0) {
                if (isNeg) {
                    if (currSpacing + 1_024 <= 0) {
                        retString += CharRepo.POS1024
                        currSpacing += 1_024
                    }
                    if (currSpacing + 512 <= 0) {
                        retString += CharRepo.POS512
                        currSpacing += 512
                    }
                    if (currSpacing + 256 <= 0) {
                        retString += CharRepo.POS256
                        currSpacing += 256
                    }
                    if (currSpacing + 128 <= 0) {
                        retString += CharRepo.POS128
                        currSpacing += 128
                    }
                    if (currSpacing + 64 <= 0) {
                        retString += CharRepo.POS64
                        currSpacing += 64
                    }
                    if (currSpacing + 32 <= 0) {
                        retString += CharRepo.POS32
                        currSpacing += 32
                    }
                    if (currSpacing + 16 <= 0) {
                        retString += CharRepo.POS16
                        currSpacing += 16
                    }
                    if (currSpacing + 8 <= 0) {
                        retString += CharRepo.POS8
                        currSpacing += 8
                    }
                    if (currSpacing + 4 <= 0) {
                        retString += CharRepo.POS4
                        currSpacing += 4
                    }
                    if (currSpacing + 2 <= 0) {
                        retString += CharRepo.POS2
                        currSpacing += 2
                    }
                    if (currSpacing + 1 <= 0) {
                        retString += CharRepo.POS1
                        currSpacing += 1
                    }
                }
                else {
                    if (currSpacing - 1_024 >= 0) {
                        retString += CharRepo.NEG1024
                        currSpacing -= 1_024
                    }
                    if (currSpacing - 512 >= 0) {
                        retString += CharRepo.NEG512
                        currSpacing -= 512
                    }
                    if (currSpacing - 256 >= 0) {
                        retString += CharRepo.NEG256
                        currSpacing -= 256
                    }
                    if (currSpacing - 128 >= 0) {
                        retString += CharRepo.NEG128
                        currSpacing -= 128
                    }
                    if (currSpacing - 64 >= 0) {
                        retString += CharRepo.NEG64
                        currSpacing -= 64
                    }
                    if (currSpacing - 32 >= 0) {
                        retString += CharRepo.NEG32
                        currSpacing -= 32
                    }
                    if (currSpacing - 16 >= 0) {
                        retString += CharRepo.NEG16
                        currSpacing -= 16
                    }
                    if (currSpacing - 8 >= 0) {
                        retString += CharRepo.NEG8
                        currSpacing -= 8
                    }
                    if (currSpacing - 4 >= 0) {
                        retString += CharRepo.NEG4
                        currSpacing -= 4
                    }
                    if (currSpacing - 2 >= 0) {
                        retString += CharRepo.NEG2
                        currSpacing -= 2
                    }
                    if (currSpacing - 1 >= 0) {
                        retString += CharRepo.NEG1
                        currSpacing -= 1
                    }
                }
            }
            return retString
        }
    }
}