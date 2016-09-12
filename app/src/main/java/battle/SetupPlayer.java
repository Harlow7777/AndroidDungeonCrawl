package battle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.dungeoncrawl.DungeonCrawl;
import com.dungeoncrawl.MusicService;

import java.io.Serializable;

import character.Barbarian;
import character.Knight;
import character.Rogue;
import character.Wizard;
import equipment.Item;

public class SetupPlayer extends GameSetup implements Serializable {

	private GameSetup g = new GameSetup();
	private boolean mIsBound = false;
	private MusicService mServ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		doBindService();
		Intent music = new Intent();
		music.setClass(this,MusicService.class);
		startService(music);
		setClassListeners();
	}

	private ServiceConnection Scon =new ServiceConnection(){
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((MusicService.ServiceBinder)binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	void doBindService() {
		bindService(new Intent(this,MusicService.class), Scon, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if(mIsBound) {
			unbindService(Scon);
			mIsBound = false;
		}
	}

	private void setClassListeners() {
		final Intent intent = new Intent(this, DungeonCrawl.class);
		final Button rogue = (Button) findViewById(R.id.rogue);
		rogue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				g.player = new Rogue("");
				intent.putExtra("Player", g.player);
				doUnbindService();
				startActivity(intent);
			}
		});
		final Button barb = (Button) findViewById(R.id.barb);
		barb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				g.player = new Barbarian("");
				intent.putExtra("Player", g.player);
				doUnbindService();
				startActivity(intent);
			}
		});
		final Button wizard = (Button) findViewById(R.id.wizard);
		wizard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				g.player = new Wizard("");
				intent.putExtra("Player", g.player);
				doUnbindService();
				startActivity(intent);
			}
		});
		final Button knight = (Button) findViewById(R.id.knight);
		knight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				g.player = new Knight("");
				doUnbindService();
				startActivity(intent);
			}
		});
	}
}

