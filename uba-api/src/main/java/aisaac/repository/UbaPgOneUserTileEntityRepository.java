package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.UbaPgOneUserTileEntity;

public interface UbaPgOneUserTileEntityRepository
		extends JpaRepository<UbaPgOneUserTileEntity, Long>, JpaSpecificationExecutor<UbaPgOneUserTileEntity> {

}
