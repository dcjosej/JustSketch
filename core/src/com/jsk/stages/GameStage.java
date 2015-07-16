package com.jsk.stages;
//package com.tfg.stages;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.ParticleEffect;
//import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
//import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.viewport.Viewport;
//
//public class GameStage extends Stage {
//
//	private Array<PooledEffect> effects = new Array();
//	private ParticleEffectPool ballExplosionPool;
//	
//	public GameStage() {
//		ParticleEffect ballExplosion = new ParticleEffect();
//		ballExplosion.load(Gdx.files.internal("Effects/ballExplosion.effect"), Gdx.files.internal("Effects"));
//		ballExplosion.scaleEffect(0.05f);
//		ballExplosionPool = new ParticleEffectPool(ballExplosion, 1, 2);
//	}
//
//	public GameStage(Viewport viewport, SpriteBatch batch) {
//		super(viewport, batch);
//		ParticleEffect ballExplosion = new ParticleEffect();
//		ballExplosion.load(Gdx.files.internal("Effects/ballExplosion.effect"), Gdx.files.internal("Effects"));
//		ballExplosion.scaleEffect(0.05f);
//		ballExplosionPool = new ParticleEffectPool(ballExplosion, 1, 2);
//	}
//
//	@Override
//	public void draw() {
//		super.draw();
//		for(PooledEffect effect : effects){
//			effect.draw(getBatch());
//			if(effect.isComplete()){
//				effect.free();
//				effects.removeValue(effect, true);
////				resetLevel();
//			}
//		}
//	}
//	
//	public void addEffect(PooledEffect effect){
//		effects.add(effect);
//	}
//	
//	public ParticleEffectPool getBallExplosionPool(){
//		return ballExplosionPool;
//	}
//}