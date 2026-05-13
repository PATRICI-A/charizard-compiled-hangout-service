package com.charizard.compiled.hangout_service.domain.ports.in;

/**
 * Puerto de entrada para el caso de uso de archivado automático.
 * Ejecutado por un scheduler para archivar parches cuya fecha ya expiró.
 */
public interface ArchiveParcheInputPort {
    /**
     * Archiva todos los parches vencidos (más de 24h desde la fecha de realización).
     *
     * @return cantidad de parches archivados
     */
    int archiveExpired();
}
