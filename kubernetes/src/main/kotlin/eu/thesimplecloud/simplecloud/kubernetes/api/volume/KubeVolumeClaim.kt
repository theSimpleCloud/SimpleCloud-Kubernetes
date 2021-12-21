package eu.thesimplecloud.simplecloud.kubernetes.api.volume

import eu.thesimplecloud.simplecloud.api.utils.Nameable

interface KubeVolumeClaim : Nameable {

    fun delete()

    interface Factory {

        fun create(
            name: String,
            volumeSpec: KubeVolumeSpec
        ): KubeVolumeClaim

    }

}