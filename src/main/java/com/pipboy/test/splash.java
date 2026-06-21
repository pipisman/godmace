import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class splash extends Entity {
    public splash(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	// The generic type must match the one of the second parameter below.
    public static final EntityDataAccessor<Integer> MY_DATA =
        SynchedEntityData.defineId(
            // The class of the entity.
            splash.class,
            // The entity data accessor type.
            EntityDataSerializers.INT
     );

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MY_DATA, 0); // default value
    }
    
	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {		
	}
    
}