package com.codex.tabe8revive;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioManager;
import android.media.MediaDescription;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebChromeClient;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.WebStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends Activity {
    private static final int REQ_LOCATION = 71;
    private static final String PREF_NOTIFY_ACCESS_HINT_SHOWN = "notify_access_hint_shown_v2";
    private static final String TAG = "ReviveBoard";
    private static final int BG_TOP = Color.rgb(5, 8, 22);
    private static final int BG_BOTTOM = Color.rgb(13, 16, 38);
    private static final int PANEL = Color.argb(218, 14, 21, 42);
    private static final int PANEL_2 = Color.argb(232, 19, 28, 54);
    private static final int LINE = Color.argb(120, 68, 92, 138);
    private static final int TEXT = Color.rgb(241, 246, 255);
    private static final int MUTED = Color.rgb(151, 166, 195);
    private static final int CYAN = Color.rgb(21, 226, 198);
    private static final int BLUE = Color.rgb(91, 181, 255);
    private static final int PINK = Color.rgb(255, 77, 166);
    private static final int AMBER = Color.rgb(255, 196, 87);
    private static final int GREEN = Color.rgb(135, 242, 170);
    private static final int RED = Color.rgb(255, 91, 118);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<TextView> allText = new ArrayList<TextView>();
    private final List<Button> allButtons = new ArrayList<Button>();
    private final List<EditText> allEdits = new ArrayList<EditText>();

    private SharedPreferences prefs;
    private FrameLayout rootView;
    private LinearLayout mainLayout;
    private TextView[] digitViews = new TextView[6];
    private TextView[] zoomDigitViews;
    private TextView dateView;
    private TextView zoomDateView;
    private TextView zoomMillisView;
    private TextView syncStatusView;
    private TextView clockStatusView;
    private TextView batteryView;
    private WeatherIconView weatherIconView;
    private TextView weatherMainView;
    private TextView weatherSubView;
    private TextView musicTitleView;
    private TextView musicSubView;
    private TextView musicInfoView;
    private AlbumArtView musicArtView;
    private IconButton musicPlayButton;
    private final List<TextView> musicTitleViews = new ArrayList<TextView>();
    private final List<TextView> musicSubViews = new ArrayList<TextView>();
    private final List<AlbumArtView> musicArtViews = new ArrayList<AlbumArtView>();
    private final List<IconButton> musicPlayButtons = new ArrayList<IconButton>();
    private final List<String> todoTexts = new ArrayList<String>();
    private final List<Boolean> todoDone = new ArrayList<Boolean>();
    private TextView timerHintView;
    private TomatoDialView tomatoDial;
    private TomatoDialView zoomTomatoDial;
    private EditText noteEdit;
    private EditText customMinutesEdit;
    private EditText todoInput;
    private LinearLayout todoListView;
    private ToggleChip keepAwakeChip;
    private ToggleChip dimChip;
    private ToggleChip[] taskChips;
    private EditText[] linkTitleEdits = new EditText[3];
    private EditText[] linkUrlEdits = new EditText[3];
    private Button startPauseButton;
    private Button zoomStartPauseButton;
    private FrameLayout zoomOverlay;
    private FrameLayout browserOverlay;
    private WebView browserWebView;
    private EditText browserAddressEdit;
    private Button browserDesktopButton;
    private Button browserTabsButton;
    private LinearLayout browserTopBar;
    private LinearLayout browserBottomBar;
    private boolean browserDesktopMode;
    private boolean browserModeLoaded;
    private boolean browserNightMode;
    private boolean browserOpen;
    private boolean browserVideoExpanded;
    private String browserCurrentUrl = "https://www.baidu.com";
    private final List<String> browserTabUrls = new ArrayList<String>();
    private final List<String> browserTabTitles = new ArrayList<String>();
    private final List<Bitmap> browserTabSnapshots = new ArrayList<Bitmap>();
    private int browserTabIndex;
    private int browserZoomPercent = 100;
    private boolean browserTabsPanelOpen;
    private final List<String> pendingBookmarkDeletes = new ArrayList<String>();
    private String browserMobileUserAgent = "";
    private View browserCustomView;
    private WebChromeClient.CustomViewCallback browserCustomViewCallback;
    private LinearLayout dockView;
    private AmbientBackgroundView ambientBackgroundView;
    private AmbientBackgroundView zoomBackgroundView;
    private MediaPlayer alertPlayer;
    private boolean alertActive;
    private boolean notificationHintShowing;
    private String activeZoomMode = "";

    private boolean timerRunning;
    private boolean timerRestMode;
    private long timerEndAt;
    private long remainingMillis;
    private int presetMinutes = 25;
    private String lastMainTime = "";
    private String lastZoomTime = "";
    private int batteryPct = -1;
    private boolean batteryCharging;
    private boolean musicPlaying;
    private long lastMusicProbeAt;
    private long musicManualStateUntil;
    private long musicIgnoreActiveUntil;
    private long appliedNotifyArtAt;
    private String musicTitle = "正在播放";
    private String musicArtist = "";
    private Bitmap musicArtBitmap;
    private String currentMusicPackage = "";
    private static volatile String latestLyricText = "";
    private static volatile long latestLyricAt;
    private static volatile String latestNotifyTitle = "";
    private static volatile String latestNotifyArtist = "";
    private static volatile String latestNotifyLyric = "";
    private static volatile String latestNotifyText = "";
    private static volatile String latestNotifyPackage = "";
    private static volatile Bitmap latestNotifyArt;
    private static volatile long latestNotifyArtAt;
    private static volatile long latestNotifyAt;
    private static volatile int latestNotifyPlayback = -1;
    private static final String[] MUSIC_PACKAGE_HINTS = new String[] {
            "qqmusic", "kugou", "netease", "kuwo", "music", "player", "audio"
    };
    private int themeIndex = 0;
    private volatile String weatherTitle = "天气加载中";
    private volatile String weatherDetail = "定位或网络不可用时会自动回退";
    private volatile int weatherCode = 2;
    private volatile Bitmap themeBitmap;
    private volatile long networkOffsetMillis;
    private volatile boolean networkTimeReady;
    private volatile boolean syncInFlight;
    private volatile String timeSourceLabel = "系统时间";

    private boolean compactLandscape() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.widthPixels > metrics.heightPixels
                && metrics.heightPixels <= dp(430);
    }

    private boolean isTabletDevice() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float widthDp = metrics.widthPixels / Math.max(1f, metrics.density);
        float heightDp = metrics.heightPixels / Math.max(1f, metrics.density);
        return Math.min(widthDp, heightDp) >= 600f;
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;
            if (level >= 0 && scale > 0) {
                batteryPct = Math.round(level * 100f / scale);
                batteryCharging = charging;
                updateBatteryText();
            }
        }
    };

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            updateTimer();
            if (!browserOpen) {
                updateClock();
                if (musicTitleView != null && SystemClock.elapsedRealtime() - lastMusicProbeAt > 8000) {
                    updateMusicFromSession();
                }
                if (musicTitleView != null || musicInfoView != null) {
                    updateMusicUi();
                }
            }
            handler.postDelayed(this, browserOpen ? 1500 : (zoomMillisView != null ? 60 : 500));
        }
    };

    private final Runnable weatherAutoRefresh = new Runnable() {
        @Override
        public void run() {
            if (!browserOpen) {
                refreshWeather(false);
            }
            handler.postDelayed(this, 30L * 60L * 1000L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("revive_board", MODE_PRIVATE);
        themeIndex = normalizeThemeIndex(prefs.getInt("theme", 0));
        prefs.edit().putInt("theme", themeIndex).apply();
        weatherTitle = prefs.getString("weather_title", weatherTitle);
        weatherDetail = prefs.getString("weather_detail", weatherDetail);
        weatherCode = prefs.getInt("weather_code", weatherCode);
        loadTodos();
        remainingMillis = presetMinutes * 60L * 1000L;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.rgb(5, 8, 22));
        }
        installImmersiveGuard();

        loadThemeImage();
        buildUi();
        rebindThemeSoon();
        loadState();
        applyKeepAwake();
        applyBrightness();
        updateClock();
        updateTimerText();
        probeMusicNowAndSoon();
        handler.postDelayed(new Runnable() {
            @Override public void run() { maybeShowNotificationAccessHint(false); }
        }, 1100);
        mainLayout.requestFocus();
        hideKeyboard();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        handler.post(tick);
        syncNetworkTime();
        requestLocationPermissionIfNeeded();
        refreshWeather();
        handler.postDelayed(weatherAutoRefresh, 30L * 60L * 1000L);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterImmersive();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        saveState();
        String restoreZoom = activeZoomMode;
        boolean restoreBrowser = browserOpen;
        String restoreUrl = currentBrowserUrl();
        detachBrowserForRebuild();
        zoomOverlay = null;
        activeZoomMode = "";
        buildUi();
        rebindThemeSoon();
        loadState();
        applyKeepAwake();
        applyBrightness();
        updateClock();
        updateTimerText();
        probeMusicNowAndSoon();
        if (restoreZoom.length() > 0) {
            showZoom(restoreZoom);
        }
        if (restoreBrowser) {
            showBrowser(restoreUrl.length() > 0 ? restoreUrl : "https://www.baidu.com");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!browserOpen) {
            probeMusicNowAndSoon();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveState();
        stopAlert();
        handler.removeCallbacks(tick);
        handler.removeCallbacks(weatherAutoRefresh);
        recycleBrowserSnapshots();
        browserTabSnapshots.clear();
        try {
            unregisterReceiver(batteryReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        if (browserCustomView != null) {
            hideBrowserCustomView();
        } else if (browserVideoExpanded) {
            exitInjectedVideoFullscreen();
        } else if (browserOverlay != null) {
            if (browserWebView != null && browserWebView.canGoBack()) {
                browserWebView.goBack();
            } else {
                closeBrowser();
            }
        } else if (zoomOverlay != null) {
            closeZoom();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION) {
            refreshWeather();
        }
    }

    private void buildUi() {
        allText.clear();
        allButtons.clear();
        allEdits.clear();
        noteEdit = null;
        customMinutesEdit = null;
        timerHintView = null;
        tomatoDial = null;
        startPauseButton = null;
        musicTitleView = null;
        musicSubView = null;
        musicInfoView = null;
        musicArtView = null;
        musicPlayButton = null;
        musicTitleViews.clear();
        musicSubViews.clear();
        musicArtViews.clear();
        musicPlayButtons.clear();
        todoInput = null;
        todoListView = null;
        linkTitleEdits = new EditText[3];
        linkUrlEdits = new EditText[3];
        taskChips = null;

        FrameLayout root = new FrameLayout(this);
        rootView = root;
        root.setBackgroundColor(BG_TOP);
        ambientBackgroundView = new AmbientBackgroundView(this);
        ambientBackgroundView.setTheme(themeIndex, themeBitmap);
        root.addView(ambientBackgroundView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setBackgroundColor(Color.TRANSPARENT);
        scroll.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dp(18), dp(22), dp(18), dp(74));
        mainLayout.setBackgroundColor(Color.TRANSPARENT);
        scroll.addView(mainLayout, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);
        mainLayout.setFocusableInTouchMode(true);
        mainLayout.requestFocus();

        mainLayout.addView(header(), matchWrap());
        addGap(12);

        boolean wide = getResources().getDisplayMetrics().widthPixels
                > getResources().getDisplayMetrics().heightPixels;
        LinearLayout stage = new LinearLayout(this);
        stage.setOrientation(wide ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        mainLayout.addView(stage, matchWrap());

        LinearLayout clockPanel = clockPanel(wide);
        LinearLayout consolePanel = consolePanel();
        LinearLayout focusPanel = focusPanel(wide);
        clockPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoom("clock");
            }
        });
        consolePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoom("console");
            }
        });
        focusPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoom("focus");
            }
        });

        if (wide) {
            int screenH = getResources().getDisplayMetrics().heightPixels;
            int stageHeight = Math.max(dp(438), screenH - dp(22 + 48 + 12 + 56 + 26));
            stage.addView(clockPanel, weightedPanel(1.08f, stageHeight));
            stage.addView(consolePanel, weightedPanel(1.18f, stageHeight));
            stage.addView(focusPanel, weightedPanel(0.95f, stageHeight));
        } else {
            stage.addView(clockPanel, verticalPanel());
            stage.addView(consolePanel, verticalPanel());
            stage.addView(focusPanel, verticalPanel());
        }

        FrameLayout.LayoutParams dockParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(56),
                Gravity.BOTTOM);
        dockParams.setMargins(dp(24), 0, dp(24), dp(8));
        dockView = bottomDock();
        root.addView(dockView, dockParams);
        enterImmersive();
        rebindThemeSoon();
    }

    private LinearLayout header() {
        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout weatherArea = row();
        weatherArea.setGravity(Gravity.CENTER_VERTICAL);
        weatherArea.setClickable(true);
        weatherArea.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showWeatherDialog(); }
        });
        weatherIconView = new WeatherIconView(this);
        weatherIconView.setWeatherCode(weatherCode);
        LinearLayout.LayoutParams markParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        markParams.setMargins(0, 0, dp(12), 0);
        weatherArea.addView(weatherIconView, markParams);

        LinearLayout titles = column();
        weatherMainView = text(weatherTitle, 24, Typeface.BOLD, TEXT);
        weatherSubView = text(weatherDetail, 13, Typeface.NORMAL, MUTED);
        titles.addView(weatherMainView, matchWrap());
        titles.addView(weatherSubView, matchWrap());
        weatherArea.addView(titles, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(weatherArea, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        updateWeatherText();

        LinearLayout status = column();
        status.setGravity(Gravity.RIGHT);
        batteryView = text("--%", 17, Typeface.BOLD, CYAN);
        batteryView.setGravity(Gravity.RIGHT);
        updateBatteryText();
        status.addView(batteryView, matchWrap());
        String systemName = systemVersionLabel();
        if (systemName.length() > 0) {
            TextView device = text(systemName, 12, Typeface.NORMAL, MUTED);
            device.setGravity(Gravity.RIGHT);
            status.addView(device, matchWrap());
        }
        header.addView(status, new LinearLayout.LayoutParams(dp(132), LinearLayout.LayoutParams.WRAP_CONTENT));
        return header;
    }

    private String systemVersionLabel() {
        try {
            String release = Build.VERSION.RELEASE;
            if (release == null || release.trim().length() == 0) {
                return "";
            }
            return "Android " + release.trim();
        } catch (Exception ex) {
            return "";
        }
    }

    private LinearLayout clockPanel(boolean wide) {
        LinearLayout panel = panel("极简翻页时钟", "", CYAN);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float scaledDensity = Math.max(1f, metrics.scaledDensity);
        int screenW = Math.max(1, metrics.widthPixels);
        int screenH = Math.max(1, metrics.heightPixels);
        int clockPanelW = wide
                ? Math.max(dp(220), Math.round(screenW * 0.28f))
                : Math.max(dp(220), screenW - dp(72));
        int usableW = Math.max(dp(160), clockPanelW - dp(36));
        int tileMargin = Math.max(dp(2), Math.min(dp(4), usableW / 150));
        int tileW = Math.max(dp(54), Math.min(dp(104), (usableW - tileMargin * 4) / 2));
        int targetH = Math.round(tileW * 0.70f);
        int maxH = wide ? Math.max(dp(54), Math.round(screenH * 0.14f)) : dp(74);
        int tileH = Math.max(dp(44), Math.min(dp(74), Math.min(targetH, maxH)));
        float digitSp = Math.max(30f, Math.min(58f, Math.min(tileW / scaledDensity * 0.58f, tileH / scaledDensity * 0.76f)));
        float dateSp = Math.max(13f, Math.min(17f, screenW / scaledDensity / 24f));
        float statusSp = Math.max(11f, Math.min(14f, screenW / scaledDensity / 30f));
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setRowCount(3);
        grid.setUseDefaultMargins(false);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setPadding(0, dp(8), 0, dp(8));
        digitViews = new TextView[6];
        for (int i = 0; i < digitViews.length; i++) {
            TextView digit = flipDigit("0", digitSp);
            digit.setGravity(Gravity.CENTER);
            digit.setIncludeFontPadding(false);
            digit.setMinWidth(0);
            digit.setMinimumWidth(0);
            digitViews[i] = digit;
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = tileW;
            p.height = tileH;
            p.setMargins(tileMargin, dp(5), tileMargin, dp(5));
            grid.addView(digit, p);
        }
        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        gridParams.gravity = Gravity.CENTER_HORIZONTAL;
        panel.addView(grid, gridParams);

        dateView = text("", dateSp, Typeface.NORMAL, MUTED);
        dateView.setPadding(0, dp(4), 0, dp(4));
        panel.addView(dateView, matchWrap());

        clockStatusView = text("", statusSp, Typeface.NORMAL, MUTED);
        clockStatusView.setPadding(dp(12), dp(9), dp(12), dp(9));
        clockStatusView.setBackground(inputBg());
        panel.addView(clockStatusView, matchWrap());
        return panel;
    }

    private LinearLayout consolePanel() {
        LinearLayout panel = panel("音乐控制台", "", BLUE);

        panel.addView(musicCard(false), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(96)));
        addPanelGap(panel, 7);

        TextView tasksTitle = text("今日待办", 15, Typeface.BOLD, TEXT);
        panel.addView(tasksTitle, matchWrap());

        LinearLayout chipsRow = row();
        chipsRow.setGravity(Gravity.CENTER_VERTICAL);
        taskChips = new ToggleChip[] {
                chip("喝水", GREEN),
                chip("走动", BLUE),
                chip("读书", AMBER),
                chip("收拾", PINK)
        };
        for (int i = 0; i < taskChips.length; i++) {
            final int index = i;
            taskChips[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskChips[index].toggle();
                    saveState();
                }
            });
            chipsRow.addView(taskChips[i], chipParams());
        }
        panel.addView(chipsRow, matchWrap());

        addPanelGap(panel, 5);
        TextView linksTitle = text("快捷入口", 15, Typeface.BOLD, TEXT);
        panel.addView(linksTitle, matchWrap());
        for (int i = 0; i < 3; i++) {
            panel.addView(linkRow(i), matchWrap());
        }
        return panel;
    }

    private LinearLayout focusPanel(boolean wide) {
        LinearLayout panel = panel("番茄钟 + 白噪音", "", PINK);
        tomatoDial = new TomatoDialView(this);
        LinearLayout.LayoutParams dialParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, wide ? dp(226) : dp(280));
        dialParams.setMargins(0, dp(6), 0, dp(4));
        panel.addView(tomatoDial, dialParams);

        timerHintView = text("25 分钟专注", 16, Typeface.BOLD, TEXT);
        timerHintView.setGravity(Gravity.CENTER);
        panel.addView(timerHintView, matchWrap());

        LinearLayout actions = row();
        startPauseButton = actionButton("开始专注", CYAN);
        Button resetButton = ghostButton("重置");
        actions.addView(startPauseButton, new LinearLayout.LayoutParams(0, dp(46), 1.35f));
        actions.addView(resetButton, new LinearLayout.LayoutParams(0, dp(46), 1f));
        panel.addView(actions, matchWrap());

        LinearLayout presets = row();
        Button workButton = ghostButton("25 分");
        Button restButton = ghostButton("5 分");
        presets.addView(workButton, new LinearLayout.LayoutParams(0, dp(42), 1f));
        presets.addView(restButton, new LinearLayout.LayoutParams(0, dp(42), 1f));
        panel.addView(presets, matchWrap());

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTimer();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerRunning = false;
                remainingMillis = presetMinutes * 60L * 1000L;
                updateTimerText();
            }
        });
        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreset(25);
            }
        });
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreset(5, true);
            }
        });
        return panel;
    }

    private LinearLayout bottomDock() {
        LinearLayout dock = row();
        dock.setGravity(Gravity.CENTER_VERTICAL);
        dock.setPadding(dp(10), dp(6), dp(10), dp(6));
        dock.setBackground(dockBg());
        if (Build.VERSION.SDK_INT >= 21) {
            dock.setElevation(dp(8));
        }

        keepAwakeChip = chip("常亮", CYAN);
        dimChip = chip("低亮", AMBER);
        ToggleChip settingsChip = chip("设置", BLUE);
        ToggleChip browserChip = chip("浏览器", PINK);
        ToggleChip themeChip = chip("主题", GREEN);

        keepAwakeChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keepAwakeChip.toggle();
                prefs.edit().putBoolean("keep_awake", keepAwakeChip.isChecked()).apply();
                applyKeepAwake();
                updateClockStatusText();
            }
        });
        dimChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimChip.toggle();
                prefs.edit().putBoolean("dim", dimChip.isChecked()).apply();
                applyBrightness();
                updateClockStatusText();
            }
        });
        settingsChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        settingsChip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                maybeShowNotificationAccessHint(true);
                return true;
            }
        });
        browserChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUri("https://www.baidu.com");
            }
        });
        themeChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTheme();
            }
        });

        dock.addView(keepAwakeChip, dockChipParams());
        dock.addView(dimChip, dockChipParams());
        dock.addView(settingsChip, dockChipParams());
        dock.addView(browserChip, dockChipParams());
        dock.addView(themeChip, dockChipParams());
        return dock;
    }

    private LinearLayout linkRow(final int index) {
        LinearLayout row = row();
        row.setGravity(Gravity.CENTER_VERTICAL);
        EditText title = compactEdit("名称");
        EditText url = compactEdit("网址");
        Button open = tinyButton("打开", CYAN);
        linkTitleEdits[index] = title;
        linkUrlEdits[index] = url;
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(dp(76), dp(42));
        titleParams.setMargins(0, dp(4), dp(6), dp(4));
        row.addView(title, titleParams);
        LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(0, dp(42), 1f);
        urlParams.setMargins(0, dp(4), dp(6), dp(4));
        row.addView(url, urlParams);
        row.addView(open, new LinearLayout.LayoutParams(dp(70), dp(42)));
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUri(linkUrlEdits[index].getText().toString());
            }
        });
        return row;
    }

    private LinearLayout musicCard(boolean large) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        boolean portrait = metrics.heightPixels > metrics.widthPixels;
        boolean compact = compactLandscape();
        int artSize = large
                ? (compact ? Math.max(dp(72), Math.min(dp(92), metrics.heightPixels / 3))
                        : (portrait ? Math.max(dp(82), Math.min(dp(112), metrics.widthPixels / 4)) : dp(126)))
                : dp(70);
        int horizontalPadding = large && (portrait || compact) ? dp(compact ? 8 : 9) : dp(large ? 12 : 8);
        int verticalPadding = large && (portrait || compact) ? dp(compact ? 7 : 8) : dp(large ? 9 : 7);
        LinearLayout card = row();
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        card.setBackground(inputBg());

        AlbumArtView art = new AlbumArtView(this);
        art.setAlbumArt(musicArtBitmap);
        art.setClickable(true);
        art.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openMusicApp(); }
        });
        art.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                maybeShowNotificationAccessHint(true);
                return true;
            }
        });
        musicArtViews.add(art);
        if (!large) {
            musicArtView = art;
        }
        LinearLayout.LayoutParams artParams = new LinearLayout.LayoutParams(
                artSize, artSize);
        artParams.setMargins(0, 0, large && (portrait || compact) ? dp(compact ? 8 : 9) : dp(large ? 14 : 9), 0);
        card.addView(art, artParams);

        LinearLayout info = column();
        TextView title = marqueeText(musicTitleForUi(), large ? (compact ? 18 : (portrait ? 19 : 23)) : 15, Typeface.BOLD, TEXT);
        TextView subtitle = marqueeText(musicSubtitleForUi(), large ? (compact ? 12 : (portrait ? 13 : 15)) : 12, Typeface.NORMAL, MUTED);
        musicTitleViews.add(title);
        musicSubViews.add(subtitle);
        if (!large) {
            musicTitleView = title;
            musicSubView = subtitle;
        }
        info.addView(title, matchWrap());
        info.addView(subtitle, matchWrap());
        addPanelGap(info, large ? (compact ? 7 : (portrait ? 8 : 13)) : 4);

        LinearLayout controls = row();
        IconButton prev = new IconButton(this, IconButton.PREVIOUS, BLUE);
        IconButton play = new IconButton(this, IconButton.PLAY, CYAN);
        IconButton next = new IconButton(this, IconButton.NEXT, BLUE);
        play.setPlaying(musicPlaying);
        musicPlayButtons.add(play);
        if (!large) {
            musicPlayButton = play;
        }
        controls.addView(prev, iconParams(large));
        controls.addView(play, iconParams(large));
        controls.addView(next, iconParams(large));
        info.addView(controls, matchWrap());

        prev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                pressFeedback(v);
                sendMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                probeMusicSoon();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                pressFeedback(v);
                boolean targetPlaying = !musicPlaying;
                musicPlaying = targetPlaying;
                long now = SystemClock.elapsedRealtime();
                musicManualStateUntil = now + 8000;
                musicIgnoreActiveUntil = targetPlaying ? 0 : now + 8000;
                updateMusicUi();
                sendMediaKey(targetPlaying
                        ? KeyEvent.KEYCODE_MEDIA_PLAY
                        : KeyEvent.KEYCODE_MEDIA_PAUSE);
                probeMusicSoon();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                pressFeedback(v);
                sendMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT);
                probeMusicSoon();
            }
        });

        card.addView(info, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        updateMusicUi();
        return card;
    }

    private LinearLayout.LayoutParams iconParams(boolean large) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        boolean portrait = metrics.heightPixels > metrics.widthPixels;
        boolean compact = compactLandscape();
        int size = large ? (compact ? dp(38) : (portrait ? dp(42) : dp(52))) : dp(30);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(size, size);
        p.setMargins(0, 0, large && (portrait || compact) ? dp(compact ? 5 : 6) : dp(large ? 10 : 5), 0);
        return p;
    }

    private void pressFeedback(View view) {
        view.animate().cancel();
        view.setScaleX(0.86f);
        view.setScaleY(0.86f);
        view.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
    }

    private void probeMusicSoon() {
        if (browserOpen) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                if (!browserOpen) {
                    updateMusicFromSession();
                }
            }
        }, 420);
    }

    private void probeMusicNowAndSoon() {
        if (browserOpen) {
            return;
        }
        updateMusicUi();
        updateMusicFromSession(true);
        int[] delays = new int[] { 150, 600, 1500 };
        for (int i = 0; i < delays.length; i++) {
            handler.postDelayed(new Runnable() {
                @Override public void run() {
                    if (!browserOpen) {
                        updateMusicFromSession(true);
                    }
                }
            }, delays[i]);
        }
    }

    private void updateMusicUi() {
        if (latestNotifyArt != null && latestNotifyArtAt > appliedNotifyArtAt) {
            musicArtBitmap = latestNotifyArt;
            appliedNotifyArtAt = latestNotifyArtAt;
        }
        if (latestNotifyTitle != null && latestNotifyTitle.length() > 0
                && (musicTitle == null || musicTitle.length() == 0 || "正在播放".equals(musicTitle) || "等待播放".equals(musicTitle))) {
            musicTitle = latestNotifyTitle;
        }
        if (latestNotifyArtist != null && latestNotifyArtist.length() > 0
                && (musicArtist == null || musicArtist.length() == 0)) {
            musicArtist = latestNotifyArtist;
        }
        String title = musicTitleForUi();
        for (int i = 0; i < musicTitleViews.size(); i++) {
            musicTitleViews.get(i).setText(title);
        }
        for (int i = 0; i < musicSubViews.size(); i++) {
            musicSubViews.get(i).setText(musicSubtitleForUi());
        }
        for (int i = 0; i < musicArtViews.size(); i++) {
            musicArtViews.get(i).setAlbumArt(musicArtBitmap);
        }
        for (int i = 0; i < musicPlayButtons.size(); i++) {
            musicPlayButtons.get(i).setPlaying(musicPlaying);
        }
        if (musicInfoView != null) {
            musicInfoView.setText(musicInfoLine());
        }
    }

    private String musicTitleForUi() {
        if (musicTitle != null && musicTitle.trim().length() > 0 && !"正在播放".equals(musicTitle)) {
            return musicTitle.trim();
        }
        if (latestNotifyTitle != null && latestNotifyTitle.trim().length() > 0) {
            return latestNotifyTitle.trim();
        }
        if (latestNotifyText != null && latestNotifyText.trim().length() > 0) {
            return firstLine(latestNotifyText.trim());
        }
        return musicPlaying ? "正在播放" : "等待播放";
    }

    private String musicSubtitleForUi() {
        if (musicArtist != null && musicArtist.trim().length() > 0 && !sameText(musicTitleForUi(), musicArtist)) {
            return musicArtist.trim();
        }
        if (latestNotifyArtist != null && latestNotifyArtist.trim().length() > 0
                && !sameText(musicTitleForUi(), latestNotifyArtist)) {
            return latestNotifyArtist.trim();
        }
        String notify = cleanMusicText(latestNotifyText);
        if (notify.length() > 0 && !sameText(musicTitleForUi(), notify) && notify.indexOf("酷狗") < 0) {
            return firstLine(notify);
        }
        if (!isNotificationAccessEnabled() && Build.VERSION.SDK_INT >= 21) {
            return "长按专辑图开启通知读取";
        }
        return "";
    }

    private void updateMusicFromSession() {
        updateMusicFromSession(false);
    }

    private void updateMusicFromSession(boolean force) {
        if (Build.VERSION.SDK_INT < 21) {
            updateMusicUi();
            return;
        }
        long now = SystemClock.elapsedRealtime();
        if (!force && now - lastMusicProbeAt < 350) {
            return;
        }
        lastMusicProbeAt = now;
        boolean manualState = now < musicManualStateUntil;
        boolean gotPlaybackState = false;
        boolean sessionPlaying = musicPlaying;
        boolean gotMetadata = false;
        String sessionPackage = "";
        try {
            MediaSessionManager manager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            List<MediaController> controllers = manager.getActiveSessions(new ComponentName(this, ReviveNotificationListener.class));
            MediaController best = null;
            int bestScore = -1;
            for (int i = 0; i < controllers.size(); i++) {
                MediaController controller = controllers.get(i);
                int score = mediaControllerScore(controller);
                if (score > bestScore) {
                    bestScore = score;
                    best = controller;
                }
            }
            if (best != null && bestScore > 0) {
                MediaController controller = best;
                sessionPackage = controller.getPackageName();
                if (sessionPackage != null && sessionPackage.length() > 0) {
                    currentMusicPackage = sessionPackage;
                }
                MediaMetadata metadata = controller.getMetadata();
                if (metadata != null) {
                    applyMediaMetadata(metadata);
                    gotMetadata = hasText(musicTitle) && !"正在播放".equals(musicTitle);
                }
                PlaybackState state = controller.getPlaybackState();
                if (state != null) {
                    gotPlaybackState = true;
                    sessionPlaying = state.getState() == PlaybackState.STATE_PLAYING
                            || state.getState() == PlaybackState.STATE_BUFFERING
                            || state.getState() == PlaybackState.STATE_CONNECTING;
                }
            }
        } catch (SecurityException ex) {
            // Active media sessions require notification access on some Android builds.
            maybeShowNotificationAccessHint(false);
        } catch (Exception ignored) {
        }
        if (latestNotifyAt > 0
                && System.currentTimeMillis() - latestNotifyAt < 10L * 60L * 1000L
                && shouldApplyLatestNotification(sessionPackage, gotMetadata)) {
            if (latestNotifyPackage != null && latestNotifyPackage.length() > 0
                    && !isSystemNotificationPackage(latestNotifyPackage)) {
                currentMusicPackage = latestNotifyPackage;
            }
            if (latestNotifyTitle != null && latestNotifyTitle.length() > 0) {
                musicTitle = latestNotifyTitle;
            }
            if (latestNotifyArtist != null && latestNotifyArtist.length() > 0) {
                musicArtist = latestNotifyArtist;
            } else if (latestNotifyText != null && latestNotifyText.length() > 0 && latestNotifyText.indexOf('\n') < 0) {
                musicArtist = latestNotifyText;
            }
            if (latestNotifyArt != null) {
                musicArtBitmap = latestNotifyArt;
            }
            if (latestNotifyPlayback >= 0 && !manualState) {
                musicPlaying = latestNotifyPlayback == 1;
            }
        }
        if (gotPlaybackState && !manualState) {
            musicPlaying = sessionPlaying;
        }
        try {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (!manualState
                    && !gotPlaybackState
                    && latestNotifyPlayback < 0
                    && now >= musicIgnoreActiveUntil
                    && audioManager != null
                    && audioManager.isMusicActive()) {
                musicPlaying = true;
            }
        } catch (Exception ignored) {
        }
        updateMusicUi();
    }

    private boolean shouldApplyLatestNotification(String sessionPackage, boolean gotMetadata) {
        if (latestNotifyPackage == null || latestNotifyPackage.length() == 0) {
            return false;
        }
        if (isSystemNotificationPackage(latestNotifyPackage)) {
            return false;
        }
        if (sessionPackage != null && sessionPackage.length() > 0) {
            return latestNotifyPackage.equals(sessionPackage) || !gotMetadata;
        }
        return true;
    }

    private static boolean isSystemNotificationPackage(String pkg) {
        if (pkg == null) {
            return true;
        }
        return "android".equals(pkg)
                || pkg.startsWith("com.android.")
                || pkg.startsWith("com.google.android.")
                || pkg.startsWith("com.google.android.ext.");
    }

    private int mediaControllerScore(MediaController controller) {
        if (controller == null) {
            return 0;
        }
        int score = 0;
        try {
            String pkg = controller.getPackageName();
            if (pkg != null && pkg.startsWith("com.android.server")) {
                score -= 30;
            }
            if (isKnownMusicPackage(pkg)) {
                score += 28;
            }
            if (pkg != null && latestNotifyPackage != null && pkg.equals(latestNotifyPackage)) {
                score += 18;
            }
            MediaMetadata metadata = controller.getMetadata();
            if (metadata != null) {
                MediaDescription description = metadata.getDescription();
                if (hasText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE))
                        || (description != null && hasText(description.getTitle()))) {
                    score += 35;
                }
                if (hasText(metadata.getText(MediaMetadata.METADATA_KEY_ARTIST))
                        || hasText(metadata.getText(MediaMetadata.METADATA_KEY_ALBUM_ARTIST))
                        || (description != null && hasText(description.getSubtitle()))) {
                    score += 10;
                }
                if (metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) != null
                        || metadata.getBitmap(MediaMetadata.METADATA_KEY_ART) != null) {
                    score += 8;
                }
            }
            PlaybackState state = controller.getPlaybackState();
            if (state != null) {
                int s = state.getState();
                if (s == PlaybackState.STATE_PLAYING
                        || s == PlaybackState.STATE_BUFFERING
                        || s == PlaybackState.STATE_CONNECTING) {
                    score += 45;
                } else if (s == PlaybackState.STATE_PAUSED
                        || s == PlaybackState.STATE_STOPPED) {
                    score += 16;
                }
            }
        } catch (Exception ignored) {
        }
        return Math.max(0, score);
    }

    private void applyMediaMetadata(MediaMetadata metadata) {
        if (metadata == null) {
            return;
        }
        MediaDescription description = metadata.getDescription();
        CharSequence title = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
        if (!hasText(title) && description != null) {
            title = description.getTitle();
        }
        if (hasText(title)) {
            musicTitle = title.toString().trim();
        }
        CharSequence artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
        if (!hasText(artist)) {
            artist = metadata.getText(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
        }
        if (!hasText(artist) && description != null) {
            artist = description.getSubtitle();
        }
        if (hasText(artist)) {
            musicArtist = artist.toString().trim();
        }
        Bitmap art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        if (art == null) {
            art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
        }
        if (art == null && description != null) {
            art = description.getIconBitmap();
        }
        if (art != null) {
            musicArtBitmap = art;
        }
    }

    private boolean hasText(CharSequence text) {
        return text != null && text.toString().trim().length() > 0;
    }

    private static boolean isKnownMusicPackage(String pkg) {
        if (pkg == null) {
            return false;
        }
        String lower = pkg.toLowerCase(Locale.US);
        for (int i = 0; i < MUSIC_PACKAGE_HINTS.length; i++) {
            if (lower.indexOf(MUSIC_PACKAGE_HINTS[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    private void loadState() {
        if (keepAwakeChip != null) {
            keepAwakeChip.setChecked(prefs.getBoolean("keep_awake", true));
        }
        if (dimChip != null) {
            dimChip.setChecked(prefs.getBoolean("dim", false));
        }
        if (noteEdit != null) {
            noteEdit.setText(prefs.getString("note", ""));
            noteEdit.clearFocus();
        }

        String[] titles = {"百度", "B站", "抖音"};
        String[] urls = {"https://www.baidu.com", "https://www.bilibili.com/", "https://www.douyin.com/"};
        String oldTitle = prefs.getString("link_title_1", "");
        String oldUrl = prefs.getString("link_url_1", "");
        String oldTitle2 = prefs.getString("link_title_2", "");
        String oldUrl2 = prefs.getString("link_url_2", "");
        String oldTitle0 = prefs.getString("link_title_0", "");
        String oldUrl0 = prefs.getString("link_url_0", "");
        boolean migrateOldBaiduLink = oldTitle0.indexOf("百度") >= 0
                || oldTitle0.toLowerCase(Locale.US).indexOf("bing") >= 0
                || oldUrl0.toLowerCase(Locale.US).indexOf("baidu") >= 0
                || oldUrl0.toLowerCase(Locale.US).indexOf("bing") >= 0;
        boolean migrateOldWeatherLink = oldTitle.indexOf("天气") >= 0
                || oldUrl.indexOf("du.cobm") >= 0
                || oldUrl.toLowerCase(Locale.US).indexOf("weather") >= 0;
        boolean migrateOldHotLink = oldTitle2.indexOf("热榜") >= 0
                || oldTitle2.indexOf("股票") >= 0
                || oldUrl2.toLowerCase(Locale.US).indexOf("tophub") >= 0
                || oldUrl2.toLowerCase(Locale.US).indexOf("10jqka") >= 0;
        for (int i = 0; i < linkTitleEdits.length; i++) {
            boolean migrate = (i == 0 && migrateOldBaiduLink)
                    || migrateOldWeatherLink
                    || (i == 2 && migrateOldHotLink);
            linkTitleEdits[i].setText(migrate
                    ? titles[i]
                    : prefs.getString("link_title_" + i, titles[i]));
            linkUrlEdits[i].setText(migrate
                    ? urls[i]
                    : prefs.getString("link_url_" + i, urls[i]));
            if (migrate) {
                prefs.edit()
                        .putString("link_title_" + i, titles[i])
                        .putString("link_url_" + i, urls[i])
                        .apply();
            }
        }

        String today = todayKey();
        String storedDay = prefs.getString("check_day", today);
        for (int i = 0; i < taskChips.length; i++) {
            taskChips[i].setChecked(today.equals(storedDay) && prefs.getBoolean("check_" + i, false));
        }

        TextWatcher saver = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { saveState(); }
        };
        if (noteEdit != null) {
            noteEdit.addTextChangedListener(saver);
        }
        for (int i = 0; i < linkTitleEdits.length; i++) {
            linkTitleEdits[i].addTextChangedListener(saver);
            linkUrlEdits[i].addTextChangedListener(saver);
        }
    }

    private void saveState() {
        if (prefs == null || taskChips == null) {
            return;
        }
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean("keep_awake", keepAwakeChip != null && keepAwakeChip.isChecked());
        e.putBoolean("dim", dimChip != null && dimChip.isChecked());
        if (noteEdit != null) {
            e.putString("note", noteEdit.getText().toString());
        }
        e.putString("check_day", todayKey());
        for (int i = 0; i < linkTitleEdits.length; i++) {
            e.putString("link_title_" + i, linkTitleEdits[i].getText().toString());
            e.putString("link_url_" + i, linkUrlEdits[i].getText().toString());
        }
        for (int i = 0; i < taskChips.length; i++) {
            e.putBoolean("check_" + i, taskChips[i].isChecked());
        }
        e.apply();
    }

    private void loadTodos() {
        todoTexts.clear();
        todoDone.clear();
        if (prefs == null) {
            addDefaultTodos();
            return;
        }
        int count = prefs.getInt("todo_count", -1);
        if (count < 0) {
            addDefaultTodos();
            saveTodos();
            return;
        }
        for (int i = 0; i < count; i++) {
            String text = prefs.getString("todo_text_" + i, "").trim();
            if (text.length() == 0) {
                continue;
            }
            todoTexts.add(text);
            todoDone.add(Boolean.valueOf(prefs.getBoolean("todo_done_" + i, false)));
        }
    }

    private void addDefaultTodos() {
        todoTexts.add("整理桌面");
        todoDone.add(Boolean.FALSE);
        todoTexts.add("喝水");
        todoDone.add(Boolean.FALSE);
        todoTexts.add("读 20 分钟");
        todoDone.add(Boolean.FALSE);
    }

    private void saveTodos() {
        if (prefs == null) {
            return;
        }
        SharedPreferences.Editor e = prefs.edit();
        int oldCount = prefs.getInt("todo_count", 0);
        e.putInt("todo_count", todoTexts.size());
        for (int i = 0; i < todoTexts.size(); i++) {
            e.putString("todo_text_" + i, todoTexts.get(i));
            e.putBoolean("todo_done_" + i, todoDone.get(i).booleanValue());
        }
        for (int i = todoTexts.size(); i < oldCount; i++) {
            e.remove("todo_text_" + i);
            e.remove("todo_done_" + i);
        }
        e.apply();
    }

    private void addTodoFromInput() {
        if (todoInput == null) {
            return;
        }
        String value = todoInput.getText().toString().trim();
        if (value.length() == 0) {
            return;
        }
        if (value.length() > 48) {
            value = value.substring(0, 48).trim();
        }
        todoTexts.add(value);
        todoDone.add(Boolean.FALSE);
        todoInput.setText("");
        saveTodos();
        refreshTodoList();
        hideKeyboard();
    }

    private void toggleTodo(int index) {
        if (index < 0 || index >= todoDone.size()) {
            return;
        }
        todoDone.set(index, Boolean.valueOf(!todoDone.get(index).booleanValue()));
        saveTodos();
        refreshTodoList();
    }

    private void deleteTodo(int index) {
        if (index < 0 || index >= todoTexts.size()) {
            return;
        }
        todoTexts.remove(index);
        todoDone.remove(index);
        saveTodos();
        refreshTodoList();
    }

    private void clearDoneTodos() {
        for (int i = todoTexts.size() - 1; i >= 0; i--) {
            if (todoDone.get(i).booleanValue()) {
                todoTexts.remove(i);
                todoDone.remove(i);
            }
        }
        saveTodos();
        refreshTodoList();
    }

    private View todoPanel() {
        boolean compact = compactLandscape();
        LinearLayout wrap = column();
        wrap.setClickable(true);

        LinearLayout addRow = row();
        addRow.setGravity(Gravity.CENTER_VERTICAL);
        todoInput = compactEdit("添加待办");
        todoInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        todoInput.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        todoInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isDone = actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEND;
                boolean isEnter = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP;
                if (isDone || isEnter) {
                    addTodoFromInput();
                    hideKeyboard();
                    todoInput.clearFocus();
                    return true;
                }
                return false;
            }
        });
        Button add = actionButton("添加", GREEN);
        int rowH = compact ? dp(38) : dp(48);
        addRow.addView(todoInput, new LinearLayout.LayoutParams(0, rowH, 1f));
        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(compact ? dp(72) : dp(88), rowH);
        addParams.setMargins(compact ? dp(6) : dp(8), 0, 0, 0);
        addRow.addView(add, addParams);
        wrap.addView(addRow, matchWrap());
        addPanelGap(wrap, compact ? 6 : 10);

        ScrollView scroll = new ScrollView(this);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.setBackground(inputBg());
        todoListView = column();
        todoListView.setPadding(compact ? dp(7) : dp(10), compact ? dp(5) : dp(8),
                compact ? dp(7) : dp(10), compact ? dp(5) : dp(8));
        scroll.addView(todoListView, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));
        wrap.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        addPanelGap(wrap, compact ? 6 : 10);

        Button clear = ghostButton("清空完成");
        wrap.addView(clear, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, compact ? dp(38) : dp(48)));

        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { addTodoFromInput(); }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { clearDoneTodos(); }
        });
        refreshTodoList();
        return wrap;
    }

    private void refreshTodoList() {
        if (todoListView == null) {
            return;
        }
        boolean compact = compactLandscape();
        todoListView.removeAllViews();
        if (todoTexts.size() == 0) {
            TextView empty = text("没有待办", compact ? 15 : 18, Typeface.BOLD, MUTED);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, compact ? dp(18) : dp(34), 0, compact ? dp(18) : dp(34));
            todoListView.addView(empty, matchWrap());
            return;
        }
        for (int i = 0; i < todoTexts.size(); i++) {
            final int index = i;
            boolean done = todoDone.get(i).booleanValue();
            LinearLayout item = row();
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(4), compact ? dp(4) : dp(7), dp(4), compact ? dp(4) : dp(7));
            item.setClickable(true);
            item.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { toggleTodo(index); }
            });

            TodoCheckView check = new TodoCheckView(this);
            check.setChecked(done);
            int checkSize = compact ? dp(26) : dp(34);
            LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(checkSize, checkSize);
            checkParams.setMargins(0, 0, compact ? dp(7) : dp(9), 0);
            item.addView(check, checkParams);

            TextView label = marqueeText(todoTexts.get(i), compact ? 15 : 19, Typeface.BOLD, done ? MUTED : TEXT);
            if (done) {
                label.setPaintFlags(label.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            item.addView(label, new LinearLayout.LayoutParams(0, compact ? dp(28) : dp(36), 1f));

            Button delete = tinyButton("删除", RED);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { deleteTodo(index); }
            });
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(compact ? dp(54) : dp(70),
                    compact ? dp(30) : dp(38));
            deleteParams.setMargins(compact ? dp(6) : dp(8), 0, 0, 0);
            item.addView(delete, deleteParams);
            todoListView.addView(item, matchWrap());
        }
    }

    private void applyKeepAwake() {
        if (keepAwakeChip != null && keepAwakeChip.isChecked()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void applyBrightness() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.screenBrightness = dimChip != null && dimChip.isChecked() ? 0.08f : -1f;
        getWindow().setAttributes(attrs);
    }

    private void updateBatteryText() {
        if (batteryView == null) {
            return;
        }
        if (batteryPct < 0) {
            batteryView.setText("电量 --");
            batteryView.setTextColor(MUTED);
            return;
        }
        String state = batteryCharging ? "充电中" : (batteryPct <= 20 ? "低电量" : "电量");
        batteryView.setText(batteryPct + "% " + state);
        batteryView.setTextColor(batteryPct <= 20 && !batteryCharging ? RED : CYAN);
    }

    private void updateClockStatusText() {
        if (clockStatusView != null) {
            String keep = keepAwakeChip != null && keepAwakeChip.isChecked() ? "常亮开" : "常亮关";
            String dim = dimChip != null && dimChip.isChecked() ? "低亮开" : "低亮关";
            clockStatusView.setText("时间源：" + timeSourceLabel + "   " + keep + "   " + dim);
        }
        if (syncStatusView != null) {
            syncStatusView.setText("时间源：" + timeSourceLabel + "   点此重新校时");
        }
    }

    private void updateWeatherText() {
        if (weatherMainView != null) {
            weatherMainView.setText(weatherTitle);
            weatherMainView.setTextColor(Color.WHITE);
            weatherMainView.setShadowLayer(dp(3), 0, dp(1), Color.argb(220, 0, 0, 0));
        }
        if (weatherSubView != null) {
            weatherSubView.setText(weatherDetail);
            weatherSubView.setTextColor(Color.rgb(235, 244, 255));
            weatherSubView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            weatherSubView.setShadowLayer(dp(3), 0, dp(1), Color.argb(230, 0, 0, 0));
        }
        if (weatherIconView != null) {
            weatherIconView.setWeatherCode(weatherCode);
        }
    }

    private void updateClock() {
        if (digitViews == null || digitViews[0] == null) {
            return;
        }
        Date now = new Date(currentAppTimeMillis());
        String time = new SimpleDateFormat("HHmmss", Locale.US).format(now);
        for (int i = 0; i < digitViews.length; i++) {
            setDigitText(digitViews[i], String.valueOf(time.charAt(i)), lastMainTime, i);
        }
        if (zoomDigitViews != null) {
            for (int i = 0; i < zoomDigitViews.length; i++) {
                setDigitText(zoomDigitViews[i], String.valueOf(time.charAt(i)), lastZoomTime, i);
            }
        }
        lastMainTime = time;
        if (zoomDigitViews != null) {
            lastZoomTime = time;
        }
        if (dateView != null) {
            dateView.setText(new SimpleDateFormat("yyyy年M月d日  EEEE", Locale.CHINA).format(now));
        }
        if (zoomDateView != null) {
            zoomDateView.setText(new SimpleDateFormat("yyyy年M月d日  EEEE", Locale.CHINA).format(now));
        }
        if (zoomMillisView != null) {
            zoomMillisView.setText(new SimpleDateFormat("SSS", Locale.US).format(now) + " ms");
        }
        updateClockStatusText();
    }

    private void setDigitText(TextView view, String value, String previous, int index) {
        if (view == null) {
            return;
        }
        String current = view.getText() == null ? "" : view.getText().toString();
        boolean changed = previous != null && previous.length() > index
                && previous.charAt(index) != value.charAt(0);
        if (changed && view instanceof FlipDigitView) {
            ((FlipDigitView) view).prepareFlip(current);
        }
        if (!value.equals(current)) {
            view.setText(value);
        }
        if (changed) {
            flipAnimate(view);
        }
    }

    private void flipAnimate(final View view) {
        if (view instanceof FlipDigitView) {
            ((FlipDigitView) view).flipFromCenter();
            return;
        }
        view.animate().cancel();
        view.setAlpha(0.72f);
        view.animate().alpha(1f).setDuration(160).start();
    }

    private void toggleTimer() {
        if (timerRunning) {
            remainingMillis = Math.max(0, timerEndAt - SystemClock.elapsedRealtime());
            timerRunning = false;
        } else {
            if (remainingMillis <= 0) {
                remainingMillis = presetMinutes * 60L * 1000L;
            }
            timerEndAt = SystemClock.elapsedRealtime() + remainingMillis;
            timerRunning = true;
        }
        updateTimerText();
    }

    private void updateTimer() {
        if (!timerRunning) {
            return;
        }
        remainingMillis = Math.max(0, timerEndAt - SystemClock.elapsedRealtime());
        updateTimerText();
        if (remainingMillis <= 0) {
            timerRunning = false;
            updateTimerText();
            showTimerDoneDialog();
        }
    }

    private void updateTimerText() {
        String modeLabel = timerRestMode ? "休息" : "专注";
        long total = presetMinutes * 60L * 1000L;
        if (tomatoDial != null) {
            tomatoDial.setTimerState(remainingMillis, total, modeLabel, timerRunning);
        }
        if (zoomTomatoDial != null) {
            zoomTomatoDial.setTimerState(remainingMillis, total, modeLabel, timerRunning);
        }
        if (timerHintView != null) {
            long minutes = Math.max(0, (remainingMillis + 59999) / 60000);
            timerHintView.setText(minutes + " 分钟" + modeLabel);
        }
        if (startPauseButton != null) {
            startPauseButton.setText(timerRunning ? "暂停" : "开始" + modeLabel);
        }
        if (zoomStartPauseButton != null) {
            zoomStartPauseButton.setText(timerRunning ? "暂停" : "开始" + modeLabel);
        }
    }

    private void setPreset(int minutes) {
        setPreset(minutes, false);
    }

    private void setPreset(int minutes, boolean restMode) {
        if (minutes < 1) {
            minutes = 1;
        }
        if (minutes > 180) {
            minutes = 180;
        }
        presetMinutes = minutes;
        timerRestMode = restMode;
        timerRunning = false;
        remainingMillis = minutes * 60L * 1000L;
        updateTimerText();
    }

    private void setCustomPreset() {
        if (customMinutesEdit == null) {
            return;
        }
        String raw = customMinutesEdit.getText().toString().trim();
        if (raw.length() == 0) {
            Toast.makeText(this, "先填分钟数", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            setPreset(Integer.parseInt(raw));
            hideKeyboard();
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "分钟数不对", Toast.LENGTH_SHORT).show();
        }
    }

    private void beep() {
        try {
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 600);
        } catch (RuntimeException ignored) {
        }
    }

    private void showTimerDoneDialog() {
        playAlert();
        final boolean wasRest = timerRestMode;
        String title = wasRest ? "休息结束" : "专注完成";
        String message = wasRest ? "该回到下一轮专注了。" : "这一轮完成了，起来活动一下。";
        try {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopAlert();
                            if (!wasRest) {
                                setPreset(5, true);
                            } else {
                                setPreset(25);
                            }
                        }
                    })
                    .create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    stopAlert();
                }
            });
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(this, title, Toast.LENGTH_LONG).show();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAlert();
            }
        }, 12000);
    }

    private void playAlert() {
        stopAlert();
        alertActive = true;
        try {
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarm == null) {
                alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            alertPlayer = MediaPlayer.create(this, alarm);
            if (alertPlayer != null) {
                alertPlayer.setLooping(true);
                alertPlayer.start();
            } else {
                repeatBeep(0);
            }
        } catch (Exception ex) {
            repeatBeep(0);
        }
        try {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(new long[] { 0, 260, 140, 260, 140, 520 }, -1);
            }
        } catch (Exception ignored) {
        }
    }

    private void repeatBeep(final int count) {
        if (!alertActive || count >= 8) {
            return;
        }
        beep();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                repeatBeep(count + 1);
            }
        }, 900);
    }

    private void stopAlert() {
        alertActive = false;
        try {
            if (alertPlayer != null) {
                alertPlayer.stop();
                alertPlayer.release();
            }
        } catch (Exception ignored) {
        }
        alertPlayer = null;
    }

    private long currentAppTimeMillis() {
        return System.currentTimeMillis() + (networkTimeReady ? networkOffsetMillis : 0L);
    }

    private void syncNetworkTime() {
        if (syncInFlight) {
            return;
        }
        syncInFlight = true;
        timeSourceLabel = "校时中";
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ok = false;
                long networkNow = 0L;
                String source = "";
                String[] hosts = new String[] { "time.cloudflare.com", "time.google.com", "pool.ntp.org" };
                for (int i = 0; i < hosts.length && !ok; i++) {
                    try {
                        networkNow = requestNtpTime(hosts[i], 2600);
                        source = "网络时间";
                        ok = networkNow > 0;
                    } catch (Exception ignored) {
                    }
                }
                if (!ok) {
                    try {
                        URLConnection conn = new URL("https://www.baidu.com").openConnection();
                        conn.setConnectTimeout(2600);
                        conn.setReadTimeout(2600);
                        conn.connect();
                        networkNow = conn.getDate();
                        source = "网络时间";
                        ok = networkNow > 0;
                    } catch (Exception ignored) {
                    }
                }
                if (ok) {
                    networkOffsetMillis = networkNow - System.currentTimeMillis();
                    networkTimeReady = true;
                    timeSourceLabel = source;
                } else {
                    networkTimeReady = false;
                    networkOffsetMillis = 0L;
                    timeSourceLabel = "系统时间";
                }
                syncInFlight = false;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateClock();
                        updateClockStatusText();
                    }
                });
            }
        }, "ReviveBoardTimeSync").start();
    }

    private long requestNtpTime(String host, int timeoutMs) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        try {
            socket.setSoTimeout(timeoutMs);
            InetAddress address = InetAddress.getByName(host);
            byte[] buffer = new byte[48];
            buffer[0] = 0x1B;
            long requestTime = System.currentTimeMillis();
            writeTimeStamp(buffer, 40, requestTime);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, 123);
            socket.send(request);
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            long responseTime = System.currentTimeMillis();
            long originate = readTimeStamp(buffer, 24);
            long receive = readTimeStamp(buffer, 32);
            long transmit = readTimeStamp(buffer, 40);
            return transmit + ((receive - originate) + (responseTime - requestTime)) / 2L;
        } finally {
            socket.close();
        }
    }

    private long readTimeStamp(byte[] buffer, int offset) {
        long seconds = read32(buffer, offset);
        long fraction = read32(buffer, offset + 4);
        return ((seconds - 2208988800L) * 1000L) + ((fraction * 1000L) / 0x100000000L);
    }

    private long read32(byte[] buffer, int offset) {
        long value = 0;
        for (int i = 0; i < 4; i++) {
            value = (value << 8) | (buffer[offset + i] & 0xff);
        }
        return value;
    }

    private void writeTimeStamp(byte[] buffer, int offset, long time) {
        long seconds = time / 1000L + 2208988800L;
        long milliseconds = time % 1000L;
        long fraction = milliseconds * 0x100000000L / 1000L;
        buffer[offset++] = (byte) (seconds >> 24);
        buffer[offset++] = (byte) (seconds >> 16);
        buffer[offset++] = (byte) (seconds >> 8);
        buffer[offset++] = (byte) seconds;
        buffer[offset++] = (byte) (fraction >> 24);
        buffer[offset++] = (byte) (fraction >> 16);
        buffer[offset++] = (byte) (fraction >> 8);
        buffer[offset] = (byte) fraction;
    }

    private void openUri(String raw) {
        saveState();
        String value = raw == null ? "" : raw.trim();
        if (value.length() == 0) {
            Toast.makeText(this, "没有网址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (value.indexOf("://") < 0) {
            value = "https://" + value;
        }
        showBrowser(value);
    }

    private void showBrowser(String url) {
        hideKeyboard();
        if (rootView == null) {
            return;
        }
        String value = url == null ? "" : url.trim();
        if (value.length() == 0) {
            value = "https://www.baidu.com";
        }
        if (value.indexOf("://") < 0) {
            value = "https://" + value;
        }
        browserOpen = true;
        browserCurrentUrl = value;
        if (browserOverlay != null) {
            updateCurrentBrowserTab(value, null);
            if (browserAddressEdit != null) {
                browserAddressEdit.setText(value);
            }
            if (browserWebView != null) {
                browserWebView.loadUrl(value);
            }
            browserOverlay.bringToFront();
            restoreImmersiveSoon();
            return;
        }
        if (!browserModeLoaded && prefs != null) {
            browserDesktopMode = prefs.getBoolean("browser_desktop", isTabletDevice());
            browserNightMode = prefs.getBoolean("browser_night", false);
            browserZoomPercent = prefs.getInt("browser_zoom_percent", 100);
            browserModeLoaded = true;
        }
        browserOverlay = new FrameLayout(this);
        browserOverlay.setClickable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            browserOverlay.setElevation(dp(36));
        }
        ensureBrowserTabs(value);
        applyBrowserShellNightMode();
        if (dockView != null) {
            dockView.setVisibility(View.GONE);
        }

        LinearLayout wrap = column();
        wrap.setPadding(dp(10), dp(8), dp(10), dp(8));
        browserOverlay.addView(wrap, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout bar = row();
        browserTopBar = bar;
        bar.setGravity(Gravity.CENTER_VERTICAL);
        browserAddressEdit = browserEdit("输入网址或搜索");
        browserAddressEdit.setText(value);
        browserAddressEdit.setSelectAllOnFocus(false);
        browserAddressEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        boolean compact = getResources().getDisplayMetrics().widthPixels < dp(520);
        int barH = compact ? dp(42) : dp(46);
        int gap = compact ? dp(6) : dp(8);
        bar.setPadding(0, 0, 0, 0);
        bar.setBackgroundColor(Color.TRANSPARENT);
        Button go = browserAccentButton("前往");
        bar.addView(go, new LinearLayout.LayoutParams(compact ? dp(64) : dp(76), barH));
        LinearLayout.LayoutParams addressParams = new LinearLayout.LayoutParams(0, barH, 1f);
        addressParams.setMargins(gap, 0, gap, 0);
        bar.addView(browserAddressEdit, addressParams);
        Button closeTop = browserButton("刷新");
        LinearLayout.LayoutParams closeTopParams = new LinearLayout.LayoutParams(compact ? dp(58) : dp(76), barH);
        bar.addView(closeTop, closeTopParams);
        wrap.addView(bar, matchWrap());
        addPanelGap(wrap, 8);

        browserWebView = new WebView(this);
        browserWebView.setBackgroundColor(Color.WHITE);
        browserWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        browserWebView.addJavascriptInterface(new BrowserVideoBridge(), "ReviveBrowser");
        WebSettings settings = browserWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(browserZoomPercent);
        browserMobileUserAgent = settings.getUserAgentString();
        applyBrowserDesktopMode();
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        // 开启 Cookie（含第三方），B 站等站点需要登录态/会话才能稳定切换清晰度
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= 21) {
                cookieManager.setAcceptThirdPartyCookies(browserWebView, true);
            }
        } catch (Exception ignored) {
        }
        browserWebView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleBrowserUrl(view, url);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= 21 && request != null && request.getUrl() != null) {
                    return handleBrowserUrl(view, request.getUrl().toString());
                }
                return false;
            }
            @Override public void onPageFinished(WebView view, String url) {
                if (browserAddressEdit != null && url != null) {
                    browserAddressEdit.setText(url);
                }
                if (url != null && url.length() > 0) {
                    browserCurrentUrl = url;
                }
                updateCurrentBrowserTab(url, view == null ? null : view.getTitle());
                applyBrowserNightMode();
                injectBrowserVideoFullscreen();
                final String finishedUrl = url;
                handler.postDelayed(new Runnable() {
                    @Override public void run() { captureBrowserTabSnapshot(finishedUrl); }
                }, 450);
                restoreImmersiveSoon();
            }
            @Override public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (isBadBrowserScheme(failingUrl)) {
                    view.stopLoading();
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= 23 && request != null && request.getUrl() != null
                        && isBadBrowserScheme(request.getUrl().toString())) {
                    view.stopLoading();
                    return;
                }
                super.onReceivedError(view, request, error);
            }
            @Override public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                handleBrowserCrashed();
                return true;
            }
        });
        browserWebView.setWebChromeClient(new WebChromeClient() {
            @Override public void onShowCustomView(View view, CustomViewCallback callback) {
                showBrowserCustomView(view, callback);
            }
            @Override public void onHideCustomView() {
                hideBrowserCustomView();
            }
        });
        wrap.addView(browserWebView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        addPanelGap(wrap, 8);
        LinearLayout nav = row();
        browserBottomBar = nav;
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setPadding(0, 0, 0, 0);
        nav.setBackgroundColor(Color.TRANSPARENT);
        Button back = browserNavButton("‹");
        Button forward = browserNavButton("›");
        Button tabs = browserNavButton(String.valueOf(Math.max(1, browserTabUrls.size())));
        browserTabsButton = tabs;
        Button menu = browserNavButton("☰");
        Button home = browserNavButton("⌂");
        nav.addView(back, browserNavParams(true));
        nav.addView(forward, browserNavParams(false));
        nav.addView(tabs, browserNavParams(false));
        nav.addView(menu, browserNavParams(false));
        nav.addView(home, browserNavParams(false));
        wrap.addView(nav, matchWrap());
        applyBrowserShellNightMode();

        View.OnClickListener loadListener = new View.OnClickListener() {
            @Override public void onClick(View v) { loadBrowserAddress(); }
        };
        go.setOnClickListener(loadListener);
        closeTop.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (browserWebView != null) {
                    browserWebView.reload();
                }
                restoreImmersiveSoon();
            }
        });
        browserAddressEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean goAction = actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE;
                boolean enter = event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP;
                if (goAction || enter) {
                    loadBrowserAddress();
                    return true;
                }
                return false;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (browserWebView != null && browserWebView.canGoBack()) {
                    browserWebView.goBack();
                }
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (browserWebView != null && browserWebView.canGoForward()) {
                    browserWebView.goForward();
                }
            }
        });
        tabs.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showBrowserTabs();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showBrowserMenu();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { closeBrowser(); }
        });

        rootView.addView(browserOverlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        browserOverlay.bringToFront();
        updateCurrentBrowserTab(value, null);
        browserWebView.loadUrl(value);
        restoreImmersiveSoon();
    }

    private void loadBrowserAddress() {
        if (browserWebView == null || browserAddressEdit == null) {
            return;
        }
        String value = browserAddressEdit.getText().toString().trim();
        if (value.length() == 0) {
            return;
        }
        if (value.indexOf("://") < 0) {
            if (value.indexOf(".") > 0 && value.indexOf(" ") < 0) {
                value = "https://" + value;
            } else {
                value = "https://www.baidu.com/s?wd=" + Uri.encode(value);
            }
        }
        hideKeyboard();
        browserCurrentUrl = value;
        updateCurrentBrowserTab(value, null);
        browserWebView.loadUrl(value);
        restoreImmersiveSoon();
    }

    private void ensureBrowserTabs(String url) {
        String value = url == null || url.trim().length() == 0 ? "https://www.baidu.com" : url.trim();
        if (browserTabUrls.size() == 0) {
            browserTabUrls.add(value);
            browserTabTitles.add("百度");
            browserTabSnapshots.add(null);
            browserTabIndex = 0;
            return;
        }
        if (browserTabIndex < 0 || browserTabIndex >= browserTabUrls.size()) {
            browserTabIndex = 0;
        }
        browserTabUrls.set(browserTabIndex, value);
    }

    private void updateCurrentBrowserTab(String url, String title) {
        if (browserTabsPanelOpen) {
            return;
        }
        if (browserTabUrls.size() == 0) {
            ensureBrowserTabs(url);
        }
        if (browserTabIndex < 0 || browserTabIndex >= browserTabUrls.size()) {
            browserTabIndex = 0;
        }
        String value = url == null || url.trim().length() == 0 ? currentBrowserUrl() : url.trim();
        if (value.length() > 0) {
            browserTabUrls.set(browserTabIndex, value);
            browserCurrentUrl = value;
        }
        String label = title == null ? "" : title.trim();
        if (label.length() == 0) {
            label = browserTitleFromUrl(value);
        }
        while (browserTabTitles.size() < browserTabUrls.size()) {
            browserTabTitles.add("新分页");
        }
        while (browserTabSnapshots.size() < browserTabUrls.size()) {
            browserTabSnapshots.add(null);
        }
        browserTabTitles.set(browserTabIndex, label);
        if (browserTabsButton != null) {
            browserTabsButton.setText(String.valueOf(Math.max(1, browserTabUrls.size())));
        }
    }

    private void captureBrowserTabSnapshot() {
        captureBrowserTabSnapshot(currentBrowserUrl());
    }

    private void captureBrowserTabSnapshot(String expectedUrl) {
        if (browserWebView == null || browserTabIndex < 0 || browserTabIndex >= browserTabUrls.size()) {
            return;
        }
        if (browserTabsPanelOpen) {
            return;
        }
        try {
            String slotUrl = browserTabUrls.get(browserTabIndex);
            String actualUrl = currentBrowserUrl();
            if (expectedUrl != null && expectedUrl.length() > 0 && !expectedUrl.equals(slotUrl)) {
                return;
            }
            if (actualUrl != null && actualUrl.length() > 0 && slotUrl != null
                    && slotUrl.length() > 0 && !actualUrl.equals(slotUrl)) {
                return;
            }
            int w = Math.max(1, browserWebView.getWidth());
            int h = Math.max(1, browserWebView.getHeight());
            if (w < dp(80) || h < dp(80)) {
                return;
            }
            int targetW = Math.max(dp(180), Math.min(dp(360), w / 2));
            int targetH = Math.max(dp(110), Math.min(dp(220), h / 2));
            Bitmap bitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            float scale = Math.min(targetW / (float) w, targetH / (float) h);
            canvas.scale(scale, scale);
            browserWebView.draw(canvas);
            while (browserTabSnapshots.size() < browserTabUrls.size()) {
                browserTabSnapshots.add(null);
            }
            Bitmap old = browserTabSnapshots.get(browserTabIndex);
            if (old != null && old != bitmap && !old.isRecycled()) {
                old.recycle();
            }
            browserTabSnapshots.set(browserTabIndex, bitmap);
        } catch (Exception ignored) {
        }
    }

    private String browserTitleFromUrl(String url) {
        if (url == null || url.length() == 0) {
            return "新分页";
        }
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            if (host != null && host.length() > 0) {
                return host.replace("www.", "");
            }
        } catch (Exception ignored) {
        }
        return url.length() > 28 ? url.substring(0, 28) : url;
    }

    private void showBrowserTabs() {
        showBrowserTabs(true);
    }

    private void showBrowserTabs(boolean captureCurrent) {
        if (captureCurrent && !browserTabsPanelOpen) {
            updateCurrentBrowserTab(currentBrowserUrl(), browserWebView == null ? null : browserWebView.getTitle());
            captureBrowserTabSnapshot();
        }
        browserTabsPanelOpen = true;

        final FrameLayout shade = new FrameLayout(this);
        shade.setBackgroundColor(Color.rgb(20, 26, 36));
        shade.setClickable(true);
        // 浏览器界面有 elevation=36，弹层必须更高，否则会被盖住导致“点了没反应”
        if (Build.VERSION.SDK_INT >= 21) {
            shade.setElevation(dp(60));
        }

        LinearLayout panel = column();
        int pad = dp(14);
        panel.setPadding(pad, dp(12), pad, dp(10));

        // 顶部标题栏
        LinearLayout head = row();
        head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text("分页 (" + browserTabUrls.size() + ")", 20, Typeface.BOLD, Color.WHITE);
        head.addView(title, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        panel.addView(head, matchWrap());
        addPanelGap(panel, 10);

        // 卡片网格（两列），可滚动
        ScrollView gridScroll = new ScrollView(this);
        gridScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout grid = column();
        int columns = 2;
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int cardGap = dp(10);
        int cardW = (screenW - pad * 2 - cardGap) / columns;
        int cardH = (int) (cardW * 0.72f);
        LinearLayout currentRow = null;
        for (int i = 0; i < browserTabUrls.size(); i++) {
            if (i % columns == 0) {
                currentRow = row();
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 0, 0, cardGap);
                grid.addView(currentRow, rowParams);
            }
            View card = browserTabCard(i, shade, cardH);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(cardW, cardH);
            if (i % columns != 0) {
                cardParams.setMargins(cardGap, 0, 0, 0);
            }
            currentRow.addView(card, cardParams);
        }
        gridScroll.addView(grid, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        panel.addView(gridScroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        addPanelGap(panel, 8);
        // 底部操作栏
        LinearLayout actions = row();
        actions.setGravity(Gravity.CENTER_VERTICAL);
        Button add = browserAccentButton("+ 新建分页");
        Button done = browserButton("完成");
        actions.addView(add, new LinearLayout.LayoutParams(0, dp(46), 2f));
        LinearLayout.LayoutParams doneParams = new LinearLayout.LayoutParams(0, dp(46), 1f);
        doneParams.setMargins(dp(10), 0, 0, 0);
        actions.addView(done, doneParams);
        panel.addView(actions, matchWrap());

        shade.addView(panel, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismissBrowserTabs(shade);
                newBrowserTab("https://www.baidu.com");
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { dismissBrowserTabs(shade); }
        });
        shade.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { dismissBrowserTabs(shade); }
        });
        panel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { }
        });
        if (rootView != null) {
            rootView.addView(shade, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            shade.bringToFront();
        }
        restoreImmersiveSoon();
    }

    private View browserTabCard(final int index, final FrameLayout shade, int cardH) {
        boolean active = index == browserTabIndex;
        final String tabUrl = index < browserTabUrls.size() ? browserTabUrls.get(index) : "";
        FrameLayout card = new FrameLayout(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(active ? 3 : 1), active ? Color.rgb(34, 197, 183) : Color.rgb(214, 222, 233));
        card.setBackground(bg);
        if (Build.VERSION.SDK_INT >= 21) {
            card.setElevation(dp(4));
            card.setClipToOutline(true);
        }

        Bitmap snapshot = index < browserTabSnapshots.size() ? browserTabSnapshots.get(index) : null;
        if (snapshot != null && !snapshot.isRecycled()) {
            SnapshotPreviewView preview = new SnapshotPreviewView(this, snapshot);
            card.addView(preview, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            View veil = new View(this);
            veil.setBackgroundColor(Color.argb(72, 255, 255, 255));
            card.addView(veil, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        LinearLayout body = column();
        body.setPadding(dp(12), dp(12), dp(12), dp(12));

        String t = index < browserTabTitles.size() ? browserTabTitles.get(index) : "";
        String url = tabUrl;
        if (t == null || t.trim().length() == 0) {
            t = browserTitleFromUrl(url);
        }
        TextView titleView = text(browserShortText(t, 30), 15, Typeface.BOLD, Color.rgb(28, 38, 52));
        titleView.setMaxLines(2);
        titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        body.addView(titleView, matchWrap());
        addPanelGap(body, 6);
        TextView urlView = text(browserShortText(browserCompactUrl(url), 42), 12, Typeface.NORMAL, Color.rgb(118, 131, 151));
        urlView.setMaxLines(2);
        urlView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        body.addView(urlView, matchWrap());
        card.addView(body, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        if (active) {
            TextView badge = text("当前", 11, Typeface.BOLD, Color.WHITE);
            badge.setGravity(Gravity.CENTER);
            badge.setPadding(dp(8), dp(2), dp(8), dp(2));
            GradientDrawable badgeBg = new GradientDrawable();
            badgeBg.setColor(Color.rgb(34, 197, 183));
            badgeBg.setCornerRadius(dp(8));
            badge.setBackground(badgeBg);
            FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM | Gravity.START);
            badgeParams.setMargins(dp(12), 0, 0, dp(12));
            card.addView(badge, badgeParams);
        }

        // 右上角关闭叉
        TextView closeX = text("✕", 16, Typeface.BOLD, Color.rgb(120, 132, 150));
        closeX.setGravity(Gravity.CENTER);
        GradientDrawable xb = new GradientDrawable();
        xb.setColor(Color.rgb(240, 243, 247));
        xb.setShape(GradientDrawable.OVAL);
        closeX.setBackground(xb);
        int xs = dp(30);
        FrameLayout.LayoutParams xParams = new FrameLayout.LayoutParams(xs, xs,
                Gravity.TOP | Gravity.END);
        xParams.setMargins(0, dp(8), dp(8), 0);
        card.addView(closeX, xParams);

        card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismissBrowserTabs(shade);
                switchBrowserTab(index);
            }
        });
        closeX.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                removeOverlayOnly(shade);
                closeBrowserTab(index, tabUrl);
                Toast.makeText(MainActivity.this, "已关闭分页", Toast.LENGTH_SHORT).show();
                showBrowserTabs(false);
            }
        });
        return card;
    }

    private void dismissBrowserTabs(FrameLayout shade) {
        removeOverlayOnly(shade);
        browserTabsPanelOpen = false;
        restoreImmersiveSoon();
    }

    private void removeOverlayOnly(FrameLayout shade) {
        try {
            if (rootView != null && shade != null) {
                rootView.removeView(shade);
            }
        } catch (Exception ignored) {
        }
    }

    private View browserDivider() {
        View v = new View(this);
        v.setBackgroundColor(Color.rgb(231, 236, 244));
        return v;
    }

    private String browserShortText(String value, int max) {
        if (value == null) {
            return "";
        }
        String clean = value.replace('\n', ' ').replace('\r', ' ').trim();
        if (clean.length() <= max) {
            return clean;
        }
        return clean.substring(0, Math.max(0, max - 1)) + "…";
    }

    private String browserCompactUrl(String value) {
        if (value == null) {
            return "";
        }
        try {
            Uri uri = Uri.parse(value);
            String host = uri.getHost();
            String path = uri.getPath();
            if (host != null && host.length() > 0) {
                String clean = host.replace("www.", "");
                if (path != null && path.length() > 1) {
                    clean += path;
                }
                return clean;
            }
        } catch (Exception ignored) {
        }
        return value;
    }

    private void recycleBrowserSnapshots() {
        for (int i = 0; i < browserTabSnapshots.size(); i++) {
            Bitmap bitmap = browserTabSnapshots.get(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    private void newBrowserTab(String url) {
        String value = url == null || url.trim().length() == 0 ? "https://www.baidu.com" : url.trim();
        captureBrowserTabSnapshot();
        updateCurrentBrowserTab(currentBrowserUrl(), browserWebView == null ? null : browserWebView.getTitle());
        browserTabUrls.add(value);
        browserTabTitles.add("百度");
        browserTabSnapshots.add(null);
        browserTabIndex = browserTabUrls.size() - 1;
        browserCurrentUrl = value;
        if (browserAddressEdit != null) {
            browserAddressEdit.setText(value);
        }
        if (browserWebView != null) {
            browserWebView.loadUrl(value);
        }
        if (browserTabsButton != null) {
            browserTabsButton.setText(String.valueOf(browserTabUrls.size()));
        }
        restoreImmersiveSoon();
    }

    private void switchBrowserTab(int index) {
        switchBrowserTab(index, true);
    }

    private void switchBrowserTab(int index, boolean saveCurrent) {
        if (index < 0 || index >= browserTabUrls.size()) {
            return;
        }
        if (saveCurrent) {
            captureBrowserTabSnapshot();
            updateCurrentBrowserTab(currentBrowserUrl(), browserWebView == null ? null : browserWebView.getTitle());
        }
        browserTabIndex = index;
        String url = browserTabUrls.get(index);
        browserCurrentUrl = url;
        if (browserAddressEdit != null) {
            browserAddressEdit.setText(url);
        }
        if (browserWebView != null) {
            browserWebView.loadUrl(url);
        }
        if (browserTabsButton != null) {
            browserTabsButton.setText(String.valueOf(Math.max(1, browserTabUrls.size())));
        }
        restoreImmersiveSoon();
    }

    private void closeCurrentBrowserTab() {
        closeBrowserTab(browserTabIndex);
    }

    private void closeBrowserTab(int index) {
        closeBrowserTab(index, index >= 0 && index < browserTabUrls.size() ? browserTabUrls.get(index) : "");
    }

    private void closeBrowserTab(int index, String expectedUrl) {
        if (browserTabUrls.size() <= 1) {
            browserTabUrls.clear();
            browserTabTitles.clear();
            recycleBrowserSnapshots();
            browserTabSnapshots.clear();
            browserTabIndex = 0;
            browserTabUrls.add("https://www.baidu.com");
            browserTabTitles.add("百度");
            browserTabSnapshots.add(null);
            switchBrowserTab(0, false);
            return;
        }
        if (index < 0 || index >= browserTabUrls.size()) {
            index = browserTabIndex >= 0 && browserTabIndex < browserTabUrls.size() ? browserTabIndex : 0;
        }
        if (expectedUrl != null && expectedUrl.length() > 0
                && (index < 0 || index >= browserTabUrls.size()
                || !expectedUrl.equals(browserTabUrls.get(index)))) {
            for (int i = 0; i < browserTabUrls.size(); i++) {
                if (expectedUrl.equals(browserTabUrls.get(i))) {
                    index = i;
                    break;
                }
            }
        }
        browserTabUrls.remove(index);
        if (index < browserTabTitles.size()) {
            browserTabTitles.remove(index);
        }
        if (index < browserTabSnapshots.size()) {
            Bitmap old = browserTabSnapshots.remove(index);
            if (old != null && !old.isRecycled()) {
                old.recycle();
            }
        }
        if (browserTabIndex > index) {
            browserTabIndex--;
        } else if (browserTabIndex == index) {
            browserTabIndex = Math.min(index, browserTabUrls.size() - 1);
        }
        if (browserTabIndex >= browserTabUrls.size()) {
            browserTabIndex = browserTabUrls.size() - 1;
        }
        switchBrowserTab(browserTabIndex, false);
    }

    private void applyBrowserDesktopMode() {
        if (browserWebView == null) {
            return;
        }
        WebSettings settings = browserWebView.getSettings();
        if (browserDesktopMode) {
            settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36");
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
        } else {
            settings.setUserAgentString(browserMobileUserAgent == null || browserMobileUserAgent.length() == 0
                    ? WebSettings.getDefaultUserAgent(this)
                    : browserMobileUserAgent);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
        }
    }

    private void applyBrowserNightMode() {
        if (browserWebView == null) {
            return;
        }
        if (browserNightMode) {
            browserWebView.setBackgroundColor(Color.BLACK);
            try {
                browserWebView.evaluateJavascript(
                        "javascript:(function(){document.documentElement.style.filter='invert(1) hue-rotate(180deg)';document.documentElement.style.background='#000';})()",
                        null);
            } catch (Exception ignored) {
                browserWebView.loadUrl("javascript:(function(){document.documentElement.style.filter='invert(1) hue-rotate(180deg)';document.documentElement.style.background='#000';})()");
            }
        } else {
            browserWebView.setBackgroundColor(Color.WHITE);
            try {
                browserWebView.evaluateJavascript(
                        "javascript:(function(){document.documentElement.style.filter='';document.documentElement.style.background='';})()",
                        null);
            } catch (Exception ignored) {
            }
        }
    }

    private void applyBrowserShellNightMode() {
        int bg = browserNightMode ? Color.rgb(8, 11, 18) : Color.rgb(247, 249, 252);
        int text = browserNightMode ? Color.rgb(230, 236, 246) : Color.rgb(45, 55, 72);
        int editText = browserNightMode ? Color.rgb(242, 246, 255) : Color.rgb(32, 41, 57);
        int muted = browserNightMode ? Color.rgb(147, 160, 184) : Color.rgb(126, 139, 158);
        if (browserOverlay != null) {
            browserOverlay.setBackgroundColor(bg);
        }
        if (browserTopBar != null) {
            browserTopBar.setBackgroundColor(Color.TRANSPARENT);
            for (int i = 0; i < browserTopBar.getChildCount(); i++) {
                View child = browserTopBar.getChildAt(i);
                if (child instanceof Button) {
                    Button b = (Button) child;
                    boolean accent = "前往".contentEquals(b.getText());
                    b.setTextColor(accent ? Color.WHITE : text);
                    if (!accent) {
                        b.setBackground(browserButtonBg(
                                browserNightMode ? Color.rgb(25, 31, 43) : Color.WHITE,
                                browserNightMode ? Color.rgb(55, 66, 88) : Color.rgb(229, 235, 243),
                                browserNightMode ? Color.rgb(35, 43, 58) : Color.rgb(247, 249, 252)));
                    }
                }
            }
        }
        if (browserBottomBar != null) {
            for (int i = 0; i < browserBottomBar.getChildCount(); i++) {
                View child = browserBottomBar.getChildAt(i);
                if (child instanceof Button) {
                    Button b = (Button) child;
                    b.setTextColor(text);
                    b.setBackground(browserButtonBg(
                            browserNightMode ? Color.rgb(25, 31, 43) : Color.WHITE,
                            browserNightMode ? Color.rgb(55, 66, 88) : Color.rgb(226, 232, 240),
                            browserNightMode ? Color.rgb(35, 43, 58) : Color.rgb(242, 246, 250)));
                }
            }
        }
        if (browserAddressEdit != null) {
            browserAddressEdit.setTextColor(editText);
            browserAddressEdit.setHintTextColor(muted);
            browserAddressEdit.setBackground(browserButtonBg(
                    browserNightMode ? Color.rgb(17, 23, 34) : Color.WHITE,
                    browserNightMode ? Color.rgb(55, 66, 88) : Color.rgb(226, 232, 240),
                    browserNightMode ? Color.rgb(27, 35, 50) : Color.rgb(247, 250, 252)));
        }
    }

    private void showBrowserMenu() {
        final String[] items = new String[] {
                browserDesktopMode ? "切换移动模式" : "切换电脑模式",
                "收藏当前页",
                "打开收藏夹",
                browserNightMode ? "切换白天模式" : "切换夜间模式",
                "页面缩放",
                "刷新页面",
                "清除浏览器缓存"
        };
        new AlertDialog.Builder(this)
                .setTitle("浏览器功能")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        handleBrowserMenu(which);
                    }
                })
                .show();
    }

    private void handleBrowserMenu(int which) {
        if (which == 0) {
            browserDesktopMode = !browserDesktopMode;
            prefs.edit().putBoolean("browser_desktop", browserDesktopMode).apply();
            applyBrowserDesktopMode();
            if (browserWebView != null) {
                browserWebView.reload();
            }
        } else if (which == 1) {
            addBrowserBookmark();
        } else if (which == 2) {
            showBrowserBookmarks();
        } else if (which == 3) {
            browserNightMode = !browserNightMode;
            prefs.edit().putBoolean("browser_night", browserNightMode).apply();
            applyBrowserNightMode();
            applyBrowserShellNightMode();
        } else if (which == 4) {
            showBrowserZoomPanel();
        } else if (which == 5) {
            if (browserWebView != null) browserWebView.reload();
        } else if (which == 6) {
            clearBrowserCache();
        }
    }

    private void showBrowserZoomPanel() {
        final TextView percent = text(browserZoomPercent + "%", 20, Typeface.BOLD,
                browserNightMode ? Color.rgb(238, 244, 255) : Color.rgb(31, 42, 58));
        percent.setGravity(Gravity.CENTER);
        LinearLayout row = row();
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(12), dp(12), dp(8));
        Button minus = browserButton("−");
        Button plus = browserButton("+");
        row.addView(minus, new LinearLayout.LayoutParams(dp(70), dp(48)));
        row.addView(percent, new LinearLayout.LayoutParams(0, dp(48), 1f));
        row.addView(plus, new LinearLayout.LayoutParams(dp(70), dp(48)));
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("页面缩放")
                .setView(row)
                .setNegativeButton("关闭", null)
                .create();
        minus.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBrowserZoom(browserZoomPercent - 10);
                percent.setText(browserZoomPercent + "%");
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBrowserZoom(browserZoomPercent + 10);
                percent.setText(browserZoomPercent + "%");
            }
        });
        dialog.show();
        restoreImmersiveSoon();
    }

    private void setBrowserZoom(int value) {
        browserZoomPercent = Math.max(50, Math.min(200, value));
        if (prefs != null) {
            prefs.edit().putInt("browser_zoom_percent", browserZoomPercent).apply();
        }
        if (browserWebView != null) {
            browserWebView.getSettings().setTextZoom(browserZoomPercent);
            try {
                browserWebView.evaluateJavascript(
                        "document.body.style.zoom='" + (browserZoomPercent / 100f) + "'",
                        null);
            } catch (Exception ignored) {
            }
        }
    }

    private void addBrowserBookmark() {
        String url = currentBrowserUrl();
        if (url.length() == 0 || url.startsWith("about:")) {
            Toast.makeText(this, "当前页面不能收藏", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = url;
        try {
            if (browserWebView != null && browserWebView.getTitle() != null
                    && browserWebView.getTitle().trim().length() > 0) {
                title = browserWebView.getTitle().trim();
            }
        } catch (Exception ignored) {
        }
        int count = prefs.getInt("browser_bookmark_count", 0);
        for (int i = 0; i < count; i++) {
            if (url.equals(prefs.getString("browser_bookmark_url_" + i, ""))) {
                Toast.makeText(this, "已经收藏过", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        prefs.edit()
                .putInt("browser_bookmark_count", count + 1)
                .putString("browser_bookmark_title_" + count, title)
                .putString("browser_bookmark_url_" + count, url)
                .apply();
        Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
    }

    private void showBrowserBookmarks() {
        final int count = prefs.getInt("browser_bookmark_count", 0);
        if (count <= 0) {
            Toast.makeText(this, "还没有收藏", Toast.LENGTH_SHORT).show();
            return;
        }
        final ArrayList<String> titles = new ArrayList<String>();
        final ArrayList<String> urls = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            String url = prefs.getString("browser_bookmark_url_" + i, "");
            if (url.length() == 0) {
                continue;
            }
            String title = prefs.getString("browser_bookmark_title_" + i, url);
            titles.add(title);
            urls.add(url);
        }
        showBookmarkSheet(titles, urls);
    }

    private void showBookmarkSheet(final ArrayList<String> titles, final ArrayList<String> urls) {
        pendingBookmarkDeletes.clear();
        final FrameLayout shade = new FrameLayout(this);
        shade.setBackgroundColor(Color.argb(132, 12, 18, 28));
        shade.setClickable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            shade.setElevation(dp(62));
        }
        LinearLayout panel = column();
        panel.setPadding(dp(16), dp(14), dp(16), dp(12));
        panel.setBackground(browserDialogBg());
        LinearLayout head = row();
        head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text("收藏夹", 20, Typeface.BOLD, Color.rgb(28, 38, 52));
        head.addView(title, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        Button done = browserButton("完成");
        head.addView(done, new LinearLayout.LayoutParams(dp(76), dp(40)));
        panel.addView(head, matchWrap());
        addPanelGap(panel, 8);
        ScrollView scroll = new ScrollView(this);
        LinearLayout list = column();
        for (int i = 0; i < urls.size(); i++) {
            list.addView(bookmarkRow(titles.get(i), urls.get(i), shade), new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(62)));
            if (i < urls.size() - 1) {
                list.addView(browserDivider(), new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
            }
        }
        scroll.addView(list, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        panel.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                Math.min(getResources().getDisplayMetrics().widthPixels - dp(48), dp(560)),
                Math.min(getResources().getDisplayMetrics().heightPixels - dp(96), dp(520)));
        panelParams.gravity = Gravity.CENTER;
        shade.addView(panel, panelParams);
        done.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { closeBookmarkSheet(shade); }
        });
        shade.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { closeBookmarkSheet(shade); }
        });
        panel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { }
        });
        if (rootView != null) {
            rootView.addView(shade, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            shade.bringToFront();
        }
        restoreImmersiveSoon();
    }

    private View bookmarkRow(final String title, final String url, final FrameLayout shade) {
        final FrameLayout wrap = new FrameLayout(this);
        LinearLayout row = row();
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), 0, dp(12), 0);
        row.setBackground(browserTabRowBg(false));
        LinearLayout texts = column();
        texts.setGravity(Gravity.CENTER_VERTICAL);
        TextView t = text(browserShortText(title, 28), 15, Typeface.BOLD, Color.rgb(30, 40, 55));
        t.setSingleLine(true);
        TextView u = text(browserShortText(browserCompactUrl(url), 42), 12, Typeface.NORMAL, Color.rgb(117, 130, 150));
        u.setSingleLine(true);
        texts.addView(t, matchWrap());
        texts.addView(u, matchWrap());
        row.addView(texts, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        final TextView star = text("★", 28, Typeface.BOLD, Color.rgb(246, 206, 116));
        star.setGravity(Gravity.CENTER);
        row.addView(star, new LinearLayout.LayoutParams(dp(48), LinearLayout.LayoutParams.MATCH_PARENT));
        wrap.addView(row, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        row.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                closeBookmarkSheet(shade);
                if (browserWebView != null) {
                    browserWebView.loadUrl(url);
                }
                browserCurrentUrl = url;
                updateCurrentBrowserTab(url, title);
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (pendingBookmarkDeletes.contains(url)) {
                    pendingBookmarkDeletes.remove(url);
                    star.setTextColor(Color.rgb(246, 206, 116));
                } else {
                    pendingBookmarkDeletes.add(url);
                    star.setTextColor(Color.rgb(170, 178, 190));
                }
            }
        });
        return wrap;
    }

    private void closeBookmarkSheet(FrameLayout shade) {
        if (pendingBookmarkDeletes.size() > 0) {
            for (int i = 0; i < pendingBookmarkDeletes.size(); i++) {
                removeBrowserBookmark(pendingBookmarkDeletes.get(i), false);
            }
            Toast.makeText(this, "已更新收藏夹", Toast.LENGTH_SHORT).show();
        }
        pendingBookmarkDeletes.clear();
        dismissBrowserTabs(shade);
    }

    private void removeBrowserBookmark(String targetUrl) {
        removeBrowserBookmark(targetUrl, true);
    }

    private void removeBrowserBookmark(String targetUrl, boolean showToast) {
        int count = prefs.getInt("browser_bookmark_count", 0);
        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<String> urls = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            String url = prefs.getString("browser_bookmark_url_" + i, "");
            if (url.length() == 0 || url.equals(targetUrl)) {
                continue;
            }
            titles.add(prefs.getString("browser_bookmark_title_" + i, url));
            urls.add(url);
        }
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < count; i++) {
            editor.remove("browser_bookmark_title_" + i);
            editor.remove("browser_bookmark_url_" + i);
        }
        editor.putInt("browser_bookmark_count", urls.size());
        for (int i = 0; i < urls.size(); i++) {
            editor.putString("browser_bookmark_title_" + i, titles.get(i));
            editor.putString("browser_bookmark_url_" + i, urls.get(i));
        }
        editor.apply();
        if (showToast) {
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearBrowserCache() {
        try {
            if (browserWebView != null) {
                browserWebView.clearCache(true);
                browserWebView.clearHistory();
                browserWebView.clearFormData();
                browserWebView.evaluateJavascript(
                        "try{localStorage.clear();sessionStorage.clear();}catch(e){}",
                        null);
            }
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= 21) {
                cookieManager.removeAllCookies(null);
                cookieManager.flush();
            } else {
                cookieManager.removeAllCookie();
            }
            WebStorage.getInstance().deleteAllData();
            WebView web = new WebView(this);
            web.clearCache(true);
            web.destroy();
        } catch (Exception ignored) {
        }
        Toast.makeText(this, "缓存、Cookie 和本地数据已清除", Toast.LENGTH_SHORT).show();
        if (browserWebView != null) {
            browserWebView.reload();
        }
    }

    private boolean handleBrowserUrl(WebView view, String url) {
        if (url == null || url.length() == 0) {
            return true;
        }
        String lower = url.toLowerCase(Locale.US);
        if (lower.startsWith("http://") || lower.startsWith("https://")
                || lower.startsWith("file://") || lower.startsWith("about:")) {
            return false;
        }
        String extracted = extractWebUrlFromSpecialScheme(url);
        if (extracted.length() > 0) {
            view.loadUrl(extracted);
            return true;
        }
        if (lower.startsWith("baiduboxapp://") || lower.startsWith("baidubrowser://")
                || lower.startsWith("qb://") || lower.startsWith("snssdk")
                || lower.startsWith("weixin://") || lower.startsWith("alipays://")) {
            Toast.makeText(this, "已拦截 App 跳转", Toast.LENGTH_SHORT).show();
            return true;
        }
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            if (intent != null) {
                String fallback = intent.getStringExtra("browser_fallback_url");
                if (fallback != null && fallback.length() > 0) {
                    view.loadUrl(fallback);
                    return true;
                }
                startActivity(intent);
                return true;
            }
        } catch (Exception ignored) {
        }
        Toast.makeText(this, "当前网页想打开外部应用", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean isBadBrowserScheme(String url) {
        if (url == null) {
            return false;
        }
        String lower = url.toLowerCase(Locale.US);
        return lower.startsWith("baiduboxapp://")
                || lower.startsWith("baidubrowser://")
                || lower.startsWith("intent://");
    }

    private String extractWebUrlFromSpecialScheme(String url) {
        try {
            Uri uri = Uri.parse(url);
            String nested = uri.getQueryParameter("url");
            if (nested == null || nested.length() == 0) {
                nested = uri.getQueryParameter("u");
            }
            if (nested != null && nested.length() > 0) {
                String decoded = Uri.decode(nested);
                if (decoded.startsWith("http://") || decoded.startsWith("https://")) {
                    return decoded;
                }
            }
        } catch (Exception ignored) {
        }
        int http = url.indexOf("http%3A");
        int https = url.indexOf("https%3A");
        int start = https >= 0 ? https : http;
        if (start >= 0) {
            String tail = Uri.decode(url.substring(start));
            int cut = tail.indexOf('&');
            if (cut > 0) {
                tail = tail.substring(0, cut);
            }
            if (tail.startsWith("http://") || tail.startsWith("https://")) {
                return tail;
            }
        }
        return "";
    }

    private void showBrowserCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (browserCustomView != null) {
            if (callback != null) {
                callback.onCustomViewHidden();
            }
            return;
        }
        browserCustomView = view;
        browserCustomViewCallback = callback;
        setBrowserChromeVisible(false);
        if (browserCustomView != null) {
            browserCustomView.setBackgroundColor(Color.BLACK);
            browserCustomView.setClickable(true);
            browserCustomView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        if (rootView != null) {
            FrameLayout.LayoutParams full = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            rootView.addView(browserCustomView, full);
            // 浏览器界面有 elevation=36，全屏层必须更高，否则会被压在下面只露黑框白边
            if (Build.VERSION.SDK_INT >= 21 && browserCustomView != null) {
                browserCustomView.setElevation(dp(64));
            }
            browserCustomView.bringToFront();
        }
        enterImmersive();
    }

    private void hideBrowserCustomView() {
        if (browserCustomView == null) {
            return;
        }
        try {
            if (rootView != null) {
                rootView.removeView(browserCustomView);
            }
            if (browserCustomViewCallback != null) {
                browserCustomViewCallback.onCustomViewHidden();
            }
        } catch (Exception ignored) {
        }
        browserCustomView = null;
        browserCustomViewCallback = null;
        setBrowserChromeVisible(true);
        enterImmersive();
    }

    private void setBrowserChromeVisible(boolean visible) {
        int state = visible ? View.VISIBLE : View.GONE;
        browserVideoExpanded = !visible;
        if (browserTopBar != null) {
            browserTopBar.setVisibility(state);
        }
        if (browserBottomBar != null) {
            browserBottomBar.setVisibility(state);
        }
        restoreImmersiveSoon();
    }

    private void exitInjectedVideoFullscreen() {
        browserVideoExpanded = false;
        setBrowserChromeVisible(true);
        if (browserWebView == null) {
            return;
        }
        String js = "(function(){"
                + "var p=document.querySelector('[data-revive-full=\"1\"]');"
                + "if(p){p.removeAttribute('data-revive-full');p.style.cssText='';}"
                + "var vs=document.querySelectorAll('video');for(var i=0;i<vs.length;i++){vs[i].style.width='';vs[i].style.height='';vs[i].style.objectFit='';}"
                + "try{if(document.exitFullscreen)document.exitFullscreen();else if(document.webkitExitFullscreen)document.webkitExitFullscreen();}catch(e){}"
                + "})();";
        try {
            browserWebView.evaluateJavascript(js, null);
        } catch (Exception ignored) {
            browserWebView.loadUrl("javascript:" + js);
        }
    }

    private void injectBrowserVideoFullscreen() {
        if (browserWebView == null) {
            return;
        }
        // 只负责：网页进入/退出原生全屏时，隐藏或恢复我们的地址栏和底部栏。
        // 不再用 JS 强行给 video 套 CSS 黑框——那样会和系统原生全屏(onShowCustomView)打架，
        // 导致 B 站等播放器布局错乱、画面冻结发黑、切换画质失败。
        String js = "(function(){"
                + "if(window.__reviveVideoFix)return;window.__reviveVideoFix=1;"
                + "function call(n){try{if(window.ReviveBrowser&&ReviveBrowser[n])ReviveBrowser[n]();}catch(e){}}"
                + "function sync(){var f=document.fullscreenElement||document.webkitFullscreenElement||document.webkitCurrentFullScreenElement;if(f){call('enterVideoFullscreen');}else{call('exitVideoFullscreen');}}"
                + "document.addEventListener('fullscreenchange',sync,true);"
                + "document.addEventListener('webkitfullscreenchange',sync,true);"
                + "})();";
        try {
            browserWebView.evaluateJavascript(js, null);
        } catch (Exception ignored) {
            browserWebView.loadUrl("javascript:" + js);
        }
    }

    public class BrowserVideoBridge {
        @JavascriptInterface
        public void enterVideoFullscreen() {
            handler.post(new Runnable() {
                @Override public void run() { setBrowserChromeVisible(false); }
            });
        }

        @JavascriptInterface
        public void exitVideoFullscreen() {
            handler.post(new Runnable() {
                @Override public void run() { setBrowserChromeVisible(true); }
            });
        }
    }

    private void closeBrowser() {
        hideKeyboard();
        hideBrowserCustomView();
        if (rootView != null && browserOverlay != null) {
            rootView.removeView(browserOverlay);
        }
        if (browserWebView != null) {
            try {
                browserWebView.stopLoading();
                browserWebView.loadUrl("about:blank");
                browserWebView.destroy();
            } catch (Exception ignored) {
            }
        }
        browserOverlay = null;
        browserWebView = null;
        browserAddressEdit = null;
        browserDesktopButton = null;
        browserTabsButton = null;
        recycleBrowserSnapshots();
        browserTabSnapshots.clear();
        browserOpen = false;
        if (dockView != null && zoomOverlay == null) {
            dockView.setVisibility(View.VISIBLE);
        }
        enterImmersive();
    }

    private String currentBrowserUrl() {
        try {
            if (browserWebView != null && browserWebView.getUrl() != null
                    && browserWebView.getUrl().length() > 0) {
                return browserWebView.getUrl();
            }
        } catch (Exception ignored) {
        }
        return browserCurrentUrl == null ? "" : browserCurrentUrl;
    }

    private void detachBrowserForRebuild() {
        hideBrowserCustomView();
        try {
            if (browserWebView != null) {
                browserCurrentUrl = currentBrowserUrl();
                browserWebView.stopLoading();
                browserWebView.loadUrl("about:blank");
                browserWebView.destroy();
            }
        } catch (Exception ignored) {
        }
        browserOverlay = null;
        browserWebView = null;
        browserAddressEdit = null;
        browserDesktopButton = null;
        browserTabsButton = null;
        browserCustomView = null;
        browserCustomViewCallback = null;
    }

    private void handleBrowserCrashed() {
        try {
            if (rootView != null && browserOverlay != null) {
                rootView.removeView(browserOverlay);
            }
        } catch (Exception ignored) {
        }
        try {
            if (browserWebView != null) {
                browserWebView.destroy();
            }
        } catch (Exception ignored) {
        }
        browserOverlay = null;
        browserWebView = null;
        browserAddressEdit = null;
        browserDesktopButton = null;
        browserTabsButton = null;
        browserCustomView = null;
        browserCustomViewCallback = null;
        browserOpen = true;
        Toast.makeText(this, "浏览器正在重建", Toast.LENGTH_SHORT).show();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                showBrowser(browserCurrentUrl.length() > 0 ? browserCurrentUrl : "https://www.baidu.com");
            }
        }, 220);
    }

    private void openMusicApp() {
        showMusicAppChooser(bestActiveMusicPackage());
    }

    private String bestActiveMusicPackage() {
        if (currentMusicPackage != null && currentMusicPackage.length() > 0
                && !isSystemNotificationPackage(currentMusicPackage)) {
            return currentMusicPackage;
        }
        if (latestNotifyPackage != null && latestNotifyPackage.length() > 0
                && !isSystemNotificationPackage(latestNotifyPackage)) {
            return latestNotifyPackage;
        }
        return "";
    }

    private boolean launchPackage(String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }
        try {
            Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launch == null) {
                return false;
            }
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void showMusicAppChooser(String preferredPackage) {
        final PackageManager pm = getPackageManager();
        final List<String> labels = new ArrayList<String>();
        final List<String> packages = new ArrayList<String>();
        addMusicPackageIfInstalled(pm, labels, packages, preferredPackage);
        addMusicPackageIfInstalled(pm, labels, packages, "com.tencent.qqmusic");
        addMusicPackageIfInstalled(pm, labels, packages, "com.kugou.android");
        addMusicPackageIfInstalled(pm, labels, packages, "com.netease.cloudmusic");
        addMusicPackageIfInstalled(pm, labels, packages, "cn.kuwo.player");
        addMusicPackageIfInstalled(pm, labels, packages, "com.miui.player");
        addMusicPackageIfInstalled(pm, labels, packages, "com.android.music");

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MUSIC);
        try {
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
            for (int i = 0; infos != null && i < infos.size(); i++) {
                ResolveInfo info = infos.get(i);
                if (info == null || info.activityInfo == null || info.activityInfo.packageName == null) {
                    continue;
                }
                addMusicPackageIfInstalled(pm, labels, packages, info.activityInfo.packageName);
            }
        } catch (Exception ignored) {
        }

        if (packages.size() == 0) {
            try {
                startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(this, "没找到音乐播放器", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (packages.size() == 1) {
            launchPackage(packages.get(0));
            return;
        }
        try {
            String activeLabel = "";
            if (preferredPackage != null && preferredPackage.length() > 0 && packages.contains(preferredPackage)) {
                activeLabel = labels.get(packages.indexOf(preferredPackage));
            }
            new AlertDialog.Builder(this)
                    .setTitle(activeLabel.length() > 0 ? "打开 " + activeLabel : "打开音乐播放器")
                    .setItems(labels.toArray(new String[labels.size()]), new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            if (which >= 0 && which < packages.size()) {
                                launchPackage(packages.get(which));
                            }
                        }
                    })
                    .show();
        } catch (Exception ex) {
            launchPackage(packages.get(0));
        }
    }

    private void addMusicPackageIfInstalled(PackageManager pm, List<String> labels, List<String> packages, String packageName) {
        if (packageName == null || packageName.length() == 0 || packages.contains(packageName)) {
            return;
        }
        Intent launch = pm.getLaunchIntentForPackage(packageName);
        if (launch == null) {
            return;
        }
        String label = packageName;
        try {
            label = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();
        } catch (Exception ignored) {
        }
        labels.add(label);
        packages.add(packageName);
    }

    private void sendMediaKey(int keyCode) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
        KeyEvent up = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0);
        audioManager.dispatchMediaKeyEvent(down);
        audioManager.dispatchMediaKeyEvent(up);
    }

    private String themeName() {
        String[] names = {"海岸", "山湖", "星空", "城市"};
        return names[themeIndex % names.length];
    }

    private int normalizeThemeIndex(int raw) {
        int count = 4;
        int value = raw % count;
        return value < 0 ? value + count : value;
    }

    private int themeResId() {
        int[] ids = {
                R.drawable.theme_coast,
                R.drawable.theme_mountain,
                R.drawable.theme_stars,
                R.drawable.theme_city
        };
        return ids[Math.max(0, themeIndex) % ids.length];
    }

    private void nextTheme() {
        themeIndex = normalizeThemeIndex(themeIndex + 1);
        prefs.edit().putInt("theme", themeIndex).apply();
        Toast.makeText(this, "主题：" + themeName(), Toast.LENGTH_SHORT).show();
        loadThemeImage();
        saveState();
        buildUi();
        loadState();
        applyKeepAwake();
        applyBrightness();
        updateClock();
        updateTimerText();
        updateMusicUi();
        updateWeatherText();
        rebindThemeSoon();
        if (ambientBackgroundView != null) {
            ambientBackgroundView.requestLayout();
        }
        if (rootView != null) {
            rootView.requestLayout();
            rootView.invalidate();
        }
        updateClock();
    }

    private void loadThemeImage() {
        final int resId = themeResId();
        InputStream input = null;
        try {
            input = getResources().openRawResource(resId);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            if (bitmap != null) {
                themeBitmap = bitmap;
                if (ambientBackgroundView != null) {
                    ambientBackgroundView.setTheme(themeIndex, bitmap);
                    ambientBackgroundView.invalidate();
                }
                if (zoomBackgroundView != null) {
                    zoomBackgroundView.setTheme(themeIndex, bitmap);
                    zoomBackgroundView.invalidate();
                }
                if (rootView != null) {
                    rootView.invalidate();
                }
            } else {
                Toast.makeText(this, "主题图片加载失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ignored) {
            Toast.makeText(this, "主题图片加载失败", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void rebindThemeSoon() {
        rebindThemeNow();
        handler.post(new Runnable() {
            @Override public void run() { rebindThemeNow(); }
        });
        handler.postDelayed(new Runnable() {
            @Override public void run() { rebindThemeNow(); }
        }, 220);
        handler.postDelayed(new Runnable() {
            @Override public void run() { rebindThemeNow(); }
        }, 820);
    }

    private void rebindThemeNow() {
        if (themeBitmap == null || themeBitmap.isRecycled()) {
            loadThemeImage();
            return;
        }
        if (ambientBackgroundView != null) {
            ambientBackgroundView.setTheme(themeIndex, themeBitmap);
            ambientBackgroundView.invalidate();
            ambientBackgroundView.requestLayout();
        }
        if (zoomBackgroundView != null) {
            zoomBackgroundView.setTheme(themeIndex, themeBitmap);
            zoomBackgroundView.invalidate();
        }
        if (rootView != null) {
            rootView.invalidate();
        }
    }

    private void requestLocationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        requestPermissions(new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQ_LOCATION);
    }

    private void refreshWeather() {
        refreshWeather(true);
    }

    private void refreshWeather(final boolean showLoading) {
        if (browserOpen) {
            return;
        }
        if (showLoading) {
            weatherTitle = "天气加载中";
            weatherDetail = "正在获取实时天气";
            updateWeatherText();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double lat = 39.9042;
                    double lon = 116.4074;
                    String place = prefs.getString("weather_place", "福州").trim();
                    String placeLabel = place.length() > 0 ? place : "福州";
                    boolean resolvedPlace = false;
                    double[] savedPos = savedWeatherPosition();
                    if (savedPos != null) {
                        lat = savedPos[0];
                        lon = savedPos[1];
                        placeLabel = prefs.getString("weather_place_label", placeLabel);
                        resolvedPlace = true;
                    }
                    if (!resolvedPlace && place.length() > 0) {
                        CityCandidate candidate = bestWeatherPlace(place);
                        if (candidate != null) {
                            lat = candidate.lat;
                            lon = candidate.lon;
                            placeLabel = candidate.label;
                            resolvedPlace = true;
                        }
                    }
                    if (!resolvedPlace) {
                        CityCandidate fallback = bestWeatherPlace("福州");
                        if (fallback != null) {
                            lat = fallback.lat;
                            lon = fallback.lon;
                            placeLabel = fallback.label;
                        }
                    }
                    String path = "/v1/forecast?latitude=" + lat
                            + "&longitude=" + lon
                            + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
                            + "&timezone=auto";
                    String json = fetchText("http://api.open-meteo.com" + path, 5000, 8000);
                    if (json == null) {
                        json = fetchText("https://api.open-meteo.com" + path, 5000, 8000);
                    }
                    if (json == null) {
                        throw new RuntimeException("weather fetch failed");
                    }
                    JSONObject root = new JSONObject(json);
                    JSONObject current = root.getJSONObject("current");
                    double temp = current.getDouble("temperature_2m");
                    int humidity = current.getInt("relative_humidity_2m");
                    int code = current.getInt("weather_code");
                    double wind = current.getDouble("wind_speed_10m");
                    weatherCode = code;
                    weatherTitle = weatherName(code) + "  " + Math.round(temp) + "°C";
                    weatherDetail = placeLabel + " · 湿度 " + humidity + "% · 风 " + Math.round(wind) + " km/h";
                    prefs.edit()
                            .putString("weather_title", weatherTitle)
                            .putString("weather_detail", weatherDetail)
                            .putInt("weather_code", weatherCode)
                            .apply();
                } catch (Exception ex) {
                    Log.e(TAG, "weather refresh failed", ex);
                    weatherTitle = prefs.getString("weather_title", "天气待更新");
                    weatherDetail = prefs.getString("weather_detail", "点击左上角设置城市");
                    weatherCode = prefs.getInt("weather_code", 2);
                }
                handler.post(new Runnable() {
                    @Override public void run() { updateWeatherText(); }
                });
            }
        }, "ReviveBoardWeather").start();
    }

    private double[] savedWeatherPosition() {
        try {
            if (!prefs.contains("weather_lat") || !prefs.contains("weather_lon")) {
                return null;
            }
            String latText = prefs.getString("weather_lat", "");
            String lonText = prefs.getString("weather_lon", "");
            if (latText.length() == 0 || lonText.length() == 0) {
                return null;
            }
            return new double[] { Double.parseDouble(latText), Double.parseDouble(lonText) };
        } catch (Exception ex) {
            return null;
        }
    }

    private CityCandidate bestWeatherPlace(String place) {
        CityCandidate exact = exactLocalWeatherPlace(place);
        if (exact != null) {
            return exact;
        }
        List<CityCandidate> results = searchWeatherPlaces(place, 1);
        return results.size() == 0 ? null : results.get(0);
    }

    private List<CityCandidate> searchWeatherPlaces(String place, int limit) {
        ArrayList<CityCandidate> results = new ArrayList<CityCandidate>();
        String query = place == null ? "" : place.trim();
        addLocalWeatherCandidates(results, query, limit);
        if (results.size() >= limit && query.length() <= 1) {
            return results;
        }
        try {
            addGeocodedWeatherPlaces(results, query.length() == 0 ? "福州" : query, "zh", limit);
            String mapped = englishWeatherQuery(query);
            if (results.size() < limit && mapped.length() > 0 && !mapped.equalsIgnoreCase(query)) {
                addGeocodedWeatherPlaces(results, mapped, "en", limit);
            }
            if (results.size() < limit) {
                addGeocodedWeatherPlaces(results, query.length() == 0 ? "Fuzhou" : query, "en", limit);
            }
        } catch (Exception ignored) {
        }
        if (results.size() == 0) {
            addLocalWeatherCandidates(results, "", limit);
        }
        while (results.size() > limit) {
            results.remove(results.size() - 1);
        }
        return results;
    }

    private void addGeocodedWeatherPlaces(List<CityCandidate> results, String query, String language, int limit) {
        if (results.size() >= limit || query == null || query.trim().length() == 0) {
            return;
        }
        try {
            String url = "http://geocoding-api.open-meteo.com/v1/search?count=" + Math.max(1, limit)
                    + "&language=" + language + "&format=json&name="
                    + URLEncoder.encode(query.trim(), "UTF-8");
            String json = fetchText(url, 4500, 6500);
            if (json == null) {
                json = fetchText(url.replace("http://", "https://"), 4500, 6500);
            }
            if (json == null) {
                return;
            }
            JSONObject root = new JSONObject(json);
            if (!root.has("results")) {
                return;
            }
            JSONArray items = root.getJSONArray("results");
            for (int i = 0; i < items.length() && results.size() < limit; i++) {
                JSONObject item = items.getJSONObject(i);
                String name = item.optString("name", "");
                if (name.length() == 0) {
                    continue;
                }
                String admin1 = item.optString("admin1", "");
                String country = item.optString("country", "");
                String label = name;
                if (admin1.length() > 0 && label.indexOf(admin1) < 0) {
                    label += " · " + admin1;
                }
                if (country.length() > 0 && label.indexOf(country) < 0) {
                    label += " · " + country;
                }
                addUniqueCity(results, new CityCandidate(
                        name,
                        label,
                        item.getDouble("latitude"),
                        item.getDouble("longitude")));
            }
        } catch (Exception ignored) {
        }
    }

    private String englishWeatherQuery(String query) {
        if (query == null) {
            return "";
        }
        String q = query.trim().toLowerCase(Locale.US);
        if (q.length() == 0) {
            return "";
        }
        for (int i = 0; i < LOCAL_CITIES.length; i++) {
            CityCandidate city = LOCAL_CITIES[i];
            String haystack = (city.name + " " + city.label + " " + city.alias).toLowerCase(Locale.US);
            if (haystack.indexOf(q) >= 0 || city.name.startsWith(query.trim())) {
                String alias = city.alias == null ? "" : city.alias.trim();
                int space = alias.indexOf(' ');
                return space > 0 ? alias.substring(0, space).trim() : alias;
            }
        }
        if ("纽约".equals(query.trim())) return "New York";
        if ("伦敦".equals(query.trim())) return "London";
        if ("巴黎".equals(query.trim())) return "Paris";
        if ("东京".equals(query.trim())) return "Tokyo";
        if ("洛杉矶".equals(query.trim())) return "Los Angeles";
        return query.trim();
    }

    private CityCandidate exactLocalWeatherPlace(String place) {
        String query = place == null ? "" : place.trim();
        if (query.length() == 0) {
            return null;
        }
        for (int i = 0; i < LOCAL_CITIES.length; i++) {
            CityCandidate city = LOCAL_CITIES[i];
            if (city.name.equals(query) || city.label.equals(query)) {
                return city;
            }
        }
        return null;
    }

    private void addLocalWeatherCandidates(List<CityCandidate> results, String query, int limit) {
        String raw = query == null ? "" : query.trim();
        String q = raw.toLowerCase(Locale.US);
        for (int i = 0; i < LOCAL_CITIES.length && results.size() < limit; i++) {
            CityCandidate city = LOCAL_CITIES[i];
            String haystack = (city.name + " " + city.label + " " + city.alias).toLowerCase(Locale.US);
            if (q.length() == 0 || haystack.indexOf(q) >= 0 || city.name.startsWith(raw)) {
                addUniqueCity(results, city);
            }
        }
    }

    private void addUniqueCity(List<CityCandidate> results, CityCandidate city) {
        for (int i = 0; i < results.size(); i++) {
            CityCandidate old = results.get(i);
            if (old.name.equals(city.name) || old.label.equals(city.label)) {
                return;
            }
        }
        results.add(city);
    }

    private double[] resolveWeatherPlace(String place) {
        CityCandidate candidate = bestWeatherPlace(place);
        if (candidate != null) {
            return new double[] { candidate.lat, candidate.lon };
        }
        try {
            List<CityCandidate> results = searchWeatherPlaces(place, 1);
            if (results.size() == 0) {
                return null;
            }
            CityCandidate first = results.get(0);
            return new double[] { first.lat, first.lon };
        } catch (Exception ex) {
            return null;
        }
    }

    private String fetchText(String url, int connectMs, int readMs) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(connectMs);
            conn.setReadTimeout(readMs);
            conn.setRequestProperty("User-Agent", "ReviveBoard/1.0 Android");
            InputStream input = conn.getInputStream();
            String text = readAll(input);
            input.close();
            return text;
        } catch (Exception ex) {
            Log.e(TAG, "fetch failed: " + url, ex);
            return null;
        }
    }

    private void showWeatherDialog() {
        showWeatherSearchDialog();
    }

    private void showWeatherSearchDialog() {
        final LinearLayout wrap = column();
        wrap.setPadding(dp(18), dp(8), dp(18), 0);
        final EditText input = compactEdit("输入城市名");
        input.setText("");
        input.setHint("输入城市名或拼音，如 北京 / bei");
        wrap.addView(input, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(46)));
        final ListView list = new ListView(this);
        final ArrayList<CityCandidate> candidates = new ArrayList<CityCandidate>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);
        wrap.addView(list, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(300)));
        final AlertDialog[] dialogRef = new AlertDialog[1];
        final int[] searchSeq = new int[] { 0 };
        final Runnable[] refreshList = new Runnable[1];
        refreshList[0] = new Runnable() {
            @Override public void run() {
                final int seq = ++searchSeq[0];
                final String query = input.getText().toString();
                ArrayList<CityCandidate> local = new ArrayList<CityCandidate>();
                addLocalWeatherCandidates(local, query, 8);
                if (local.size() == 0) {
                    addLocalWeatherCandidates(local, "", 8);
                }
                updateCityCandidateList(candidates, adapter, local);
                new Thread(new Runnable() {
                    @Override public void run() {
                        final List<CityCandidate> found = searchWeatherPlaces(query, 8);
                        handler.post(new Runnable() {
                            @Override public void run() {
                                if (seq == searchSeq[0]
                                        && query.equals(input.getText().toString())
                                        && found.size() > 0) {
                                    ArrayList<CityCandidate> merged = new ArrayList<CityCandidate>();
                                    addLocalWeatherCandidates(merged, query, 8);
                                    for (int i = 0; i < found.size() && merged.size() < 8; i++) {
                                        addUniqueCity(merged, found.get(i));
                                    }
                                    updateCityCandidateList(candidates, adapter, merged);
                                }
                            }
                        });
                    }
                }, "ReviveBoardCitySearch").start();
            }
        };
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(refreshList[0]);
                handler.postDelayed(refreshList[0], 260);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < candidates.size()) {
                    applyWeatherPlace(candidates.get(position));
                    if (dialogRef[0] != null) {
                        dialogRef[0].dismiss();
                    }
                }
            }
        });
        try {
            dialogRef[0] = new AlertDialog.Builder(this)
                    .setTitle("搜索城市")
                    .setView(wrap)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (candidates.size() > 0) {
                                applyWeatherPlace(candidates.get(0));
                            } else {
                                applyWeatherPlace(input.getText().toString());
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            refreshList[0].run();
        } catch (Exception ex) {
            Toast.makeText(this, "搜索打不开", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyWeatherPlace(String place) {
        CityCandidate candidate = bestWeatherPlace(place);
        if (candidate != null) {
            applyWeatherPlace(candidate);
            return;
        }
        String value = place == null ? "" : place.trim();
        if (value.length() == 0) {
            value = "福州";
        }
        prefs.edit()
                .putString("weather_place", value)
                .remove("weather_lat")
                .remove("weather_lon")
                .remove("weather_place_label")
                .apply();
        refreshWeather(true);
    }

    private void applyWeatherPlace(CityCandidate candidate) {
        prefs.edit()
                .putString("weather_place", candidate.name)
                .putString("weather_place_label", candidate.label)
                .putString("weather_lat", String.valueOf(candidate.lat))
                .putString("weather_lon", String.valueOf(candidate.lon))
                .apply();
        refreshWeather(true);
    }

    private void updateCityCandidateList(ArrayList<CityCandidate> candidates,
            ArrayAdapter<String> adapter, List<CityCandidate> values) {
        candidates.clear();
        candidates.addAll(values);
        adapter.clear();
        for (int i = 0; i < candidates.size(); i++) {
            adapter.add(candidates.get(i).label);
        }
        adapter.notifyDataSetChanged();
    }

    private String readAll(InputStream input) throws Exception {
        StringBuilder builder = new StringBuilder();
        byte[] buf = new byte[4096];
        int n;
        while ((n = input.read(buf)) >= 0) {
            builder.append(new String(buf, 0, n, "UTF-8"));
        }
        return builder.toString();
    }

    private String weatherName(int code) {
        if (code == 0) return "晴";
        if (code <= 3) return "多云";
        if (code == 45 || code == 48) return "雾";
        if (code >= 51 && code <= 67) return "雨";
        if (code >= 71 && code <= 77) return "雪";
        if (code >= 80 && code <= 82) return "阵雨";
        if (code >= 95) return "雷雨";
        return "天气";
    }

    private void openHomeSettings() {
        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
        try {
            startActivity(intent);
        } catch (Exception ex) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
            Toast.makeText(this, "请选择默认桌面应用", Toast.LENGTH_LONG).show();
        }
    }

    private void showZoom(String mode) {
        hideKeyboard();
        if (rootView == null) {
            return;
        }
        activeZoomMode = mode == null ? "" : mode;
        if (zoomOverlay != null) {
            rootView.removeView(zoomOverlay);
        }
        zoomOverlay = new FrameLayout(this);
        zoomOverlay.setClickable(true);
        zoomOverlay.setFocusable(true);
        zoomOverlay.setFocusableInTouchMode(true);
        zoomOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            zoomOverlay.setElevation(dp(30));
        }
        zoomOverlay.setBackgroundColor(Color.rgb(4, 7, 18));
        if (dockView != null) {
            dockView.setVisibility(View.GONE);
        }
        zoomBackgroundView = new AmbientBackgroundView(this);
        zoomBackgroundView.setTheme(themeIndex, themeBitmap);
        zoomOverlay.addView(zoomBackgroundView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        boolean compact = compactLandscape();
        LinearLayout content = column();
        content.setClickable(true);
        content.setFocusableInTouchMode(true);
        content.setPadding(compact ? dp(16) : dp(26), compact ? dp(10) : dp(22),
                compact ? dp(16) : dp(26), compact ? dp(12) : dp(24));
        zoomOverlay.addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text(zoomTitle(mode), compact ? 21 : 24, Typeface.BOLD, TEXT);
        top.addView(title, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        Button close = ghostButton("返回");
        top.addView(close, new LinearLayout.LayoutParams(compact ? dp(74) : dp(92),
                compact ? dp(36) : dp(44)));
        content.addView(top, matchWrap());
        addPanelGap(content, compact ? 7 : 12);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeZoom();
            }
        });

        if ("clock".equals(mode)) {
            content.addView(zoomClock(), new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        } else if ("focus".equals(mode)) {
            content.addView(zoomFocus(), new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        } else {
            content.addView(zoomConsole(), new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        }

        rootView.addView(zoomOverlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        zoomOverlay.bringToFront();
        updateClock();
        updateTimerText();
        enterImmersive();
    }

    private String zoomTitle(String mode) {
        if ("clock".equals(mode)) {
            return "全屏翻页时钟";
        }
        if ("focus".equals(mode)) {
            return "专注舱";
        }
        return "音乐与待办";
    }

    private void closeZoom() {
        saveState();
        hideKeyboard();
        if (rootView != null && zoomOverlay != null) {
            rootView.removeView(zoomOverlay);
        }
        if (dockView != null) {
            dockView.setVisibility(View.VISIBLE);
        }
        zoomOverlay = null;
        activeZoomMode = "";
        zoomDigitViews = null;
        zoomDateView = null;
        zoomMillisView = null;
        syncStatusView = null;
        zoomTomatoDial = null;
        zoomBackgroundView = null;
        zoomStartPauseButton = null;
        customMinutesEdit = null;
        buildUi();
        loadState();
        applyKeepAwake();
        applyBrightness();
        updateClock();
        updateTimerText();
        probeMusicNowAndSoon();
        enterImmersive();
    }

    private View zoomClock() {
        lastZoomTime = "";
        LinearLayout wrap = column();
        wrap.setClickable(true);
        wrap.setGravity(Gravity.CENTER);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(6);
        grid.setRowCount(1);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenW = Math.max(1, metrics.widthPixels);
        int screenH = Math.max(1, metrics.heightPixels);
        float scaledDensity = Math.max(1f, metrics.scaledDensity);
        boolean landscape = screenW >= screenH;
        int horizontalPadding = dp(52);
        int usableW = Math.max(dp(240), screenW - horizontalPadding);
        int usableH = Math.max(dp(220), screenH - dp(170));
        int tileMargin = Math.max(dp(2), Math.min(dp(6), usableW / 180));
        int tileW = Math.max(dp(42), Math.min(dp(136), (usableW - tileMargin * 12) / 6));
        int targetH = Math.round(tileW * 1.30f);
        int maxH = Math.max(dp(62), usableH - dp(84));
        int tileH = Math.max(dp(58), Math.min(dp(176), Math.min(targetH, maxH)));
        float tileWSp = tileW / scaledDensity;
        float tileHSp = tileH / scaledDensity;
        float screenWSp = screenW / scaledDensity;
        float screenHSp = screenH / scaledDensity;
        float digitSp = Math.max(26f, Math.min(94f, Math.min(tileWSp * 0.62f, tileHSp * 0.56f)));
        float dateSp = landscape
                ? Math.max(18f, Math.min(30f, screenHSp / 24f))
                : Math.max(16f, Math.min(22f, screenWSp / 18f));
        float millisSp = landscape
                ? Math.max(13f, Math.min(20f, screenHSp / 36f))
                : Math.max(12f, Math.min(16f, screenWSp / 27f));

        zoomDigitViews = new TextView[6];
        for (int i = 0; i < zoomDigitViews.length; i++) {
            TextView digit = flipDigit("0", digitSp);
            digit.setGravity(Gravity.CENTER);
            digit.setIncludeFontPadding(false);
            digit.setMinWidth(0);
            digit.setMinimumWidth(0);
            zoomDigitViews[i] = digit;
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = tileW;
            p.height = tileH;
            p.setMargins(tileMargin, 0, tileMargin, 0);
            grid.addView(digit, p);
        }
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        gp.gravity = Gravity.CENTER_HORIZONTAL;
        wrap.addView(grid, gp);

        zoomDateView = text("", dateSp, Typeface.BOLD, TEXT);
        zoomDateView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dpv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dpv.setMargins(0, dp(22), 0, dp(4));
        wrap.addView(zoomDateView, dpv);

        zoomMillisView = text("000 ms", millisSp, Typeface.NORMAL, CYAN);
        zoomMillisView.setGravity(Gravity.CENTER);
        zoomMillisView.setAlpha(0.72f);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mp.setMargins(0, 0, 0, dp(10));
        wrap.addView(zoomMillisView, mp);

        syncStatusView = text("时间源：" + timeSourceLabel + "   点此重新校时", 18, Typeface.NORMAL, MUTED);
        final TextView source = syncStatusView;
        source.setGravity(Gravity.CENTER);
        source.setClickable(true);
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                syncNetworkTime();
            }
        });
        wrap.addView(source, matchWrap());
        return wrap;
    }

    private View zoomConsole() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        boolean wide = metrics.widthPixels >= metrics.heightPixels;
        boolean compact = compactLandscape();
        LinearLayout wrap = wide ? row() : column();
        wrap.setClickable(true);
        wrap.setGravity(wide ? Gravity.CENTER_VERTICAL : Gravity.CENTER_HORIZONTAL);

        LinearLayout left = panel("音乐遥控", "", BLUE);
        int musicCardHeight = compact ? Math.max(dp(112), Math.min(dp(132), metrics.heightPixels / 3))
                : (wide ? dp(162) : Math.max(dp(124), Math.min(dp(150), metrics.heightPixels / 7)));
        left.addView(musicCard(true), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, musicCardHeight));
        addPanelGap(left, compact ? 8 : (wide ? 18 : 10));

        TextView lyric = text(musicInfoLine(), compact ? 16 : (wide ? 24 : 19), Typeface.BOLD, TEXT);
        lyric.setGravity(Gravity.CENTER);
        lyric.setPadding(dp(compact ? 10 : 14), compact ? dp(8) : (wide ? dp(18) : dp(12)),
                dp(compact ? 10 : 14), compact ? dp(8) : (wide ? dp(18) : dp(12)));
        lyric.setBackground(metricBg(PINK));
        lyric.setSingleLine(false);
        lyric.setIncludeFontPadding(true);
        musicInfoView = lyric;
        left.addView(lyric, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, wide ? (compact ? dp(56) : dp(128)) : 0,
                wide ? 0f : 1f));

        LinearLayout right = panel("待办事项", "", PINK);
        right.addView(todoPanel(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        if (wide) {
            wrap.addView(left, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,
                    compact ? 1.02f : 1.15f));
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,
                    compact ? 1.12f : 1f);
            rp.setMargins(compact ? dp(8) : dp(12), 0, 0, 0);
            wrap.addView(right, rp);
        } else {
            wrap.addView(left, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.86f));
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
            rp.setMargins(0, dp(12), 0, 0);
            wrap.addView(right, rp);
        }
        return wrap;
    }

    private View zoomFocus() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        boolean wide = metrics.widthPixels >= metrics.heightPixels;
        boolean compact = compactLandscape();
        LinearLayout wrap = wide ? row() : column();
        wrap.setClickable(true);
        wrap.setGravity(wide ? Gravity.CENTER_VERTICAL : Gravity.CENTER_HORIZONTAL);

        LinearLayout left = panel("专注仪表盘", "", PINK);
        zoomTomatoDial = new TomatoDialView(this);
        left.addView(zoomTomatoDial, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        if (wide) {
            wrap.addView(left, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,
                    compact ? 1.05f : 1.25f));
        } else {
            wrap.addView(left, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.18f));
        }

        LinearLayout controls = panel("时间设定", "", CYAN);
        int buttonH = compact ? dp(38) : (wide ? dp(56) : Math.max(dp(44), Math.min(dp(52), metrics.heightPixels / 15)));
        int inputH = compact ? dp(34) : (wide ? dp(48) : Math.max(dp(42), Math.min(dp(50), metrics.heightPixels / 16)));
        zoomStartPauseButton = actionButton("开始专注", CYAN);
        controls.addView(zoomStartPauseButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, buttonH));
        addPanelGap(controls, compact ? 5 : (wide ? 10 : 7));
        LinearLayout presetRow = row();
        Button p25 = ghostButton("25 分");
        Button p5 = ghostButton("5 分");
        Button p45 = ghostButton("45 分");
        presetRow.addView(p25, new LinearLayout.LayoutParams(0, inputH, 1f));
        presetRow.addView(p5, new LinearLayout.LayoutParams(0, inputH, 1f));
        presetRow.addView(p45, new LinearLayout.LayoutParams(0, inputH, 1f));
        controls.addView(presetRow, matchWrap());
        addPanelGap(controls, compact ? 5 : (wide ? 12 : 8));

        LinearLayout customRow = row();
        customMinutesEdit = compactEdit("分钟");
        customMinutesEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button set = actionButton("设定", AMBER);
        customRow.addView(customMinutesEdit, new LinearLayout.LayoutParams(0, inputH, 1f));
        LinearLayout.LayoutParams setParams = new LinearLayout.LayoutParams(compact ? dp(68) : (wide ? dp(96) : dp(82)), inputH);
        setParams.setMargins(compact ? dp(6) : dp(8), 0, 0, 0);
        customRow.addView(set, setParams);
        controls.addView(customRow, matchWrap());
        addPanelGap(controls, compact ? 5 : (wide ? 14 : 8));

        Button reset = ghostButton("重置当前计时");
        controls.addView(reset, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, inputH));

        zoomStartPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { toggleTimer(); }
        });
        p25.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { setPreset(25); }
        });
        p5.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { setPreset(5, true); }
        });
        p45.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { setPreset(45); }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { setCustomPreset(); }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                timerRunning = false;
                remainingMillis = presetMinutes * 60L * 1000L;
                updateTimerText();
            }
        });

        LinearLayout.LayoutParams cp = wide
                ? new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, compact ? 0.95f : 0.82f)
                : new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.82f);
        cp.setMargins(wide ? (compact ? dp(8) : dp(12)) : 0, wide ? 0 : dp(12), 0, 0);
        wrap.addView(controls, cp);
        return wrap;
    }

    private String musicInfoLine() {
        String title = musicTitleForUi();
        String lyric = cleanMusicText(latestLyricText);
        String notifyLyric = cleanMusicText(latestNotifyLyric);
        if (notifyLyric.length() > 0 && !sameText(title, notifyLyric)) {
            return title + "\n" + notifyLyric;
        }
        if (lyric.length() > 0
                && System.currentTimeMillis() - latestLyricAt < 20L * 1000L
                && !sameText(title, lyric)) {
            return title + "\n" + lyric;
        }
        String notify = cleanMusicText(latestNotifyText);
        if (notify.length() > 0 && !sameText(title, notify) && notify.indexOf("酷狗") < 0) {
            return title + "\n" + notify;
        }
        if (musicArtist != null && musicArtist.trim().length() > 0 && !sameText(title, musicArtist)) {
            return title + "\n" + musicArtist.trim();
        }
        if (title.length() > 0 && !"正在播放".equals(title) && !"等待播放".equals(title)) {
            String subtitle = musicSubtitleForUi();
            if (subtitle.length() > 0 && !sameText(title, subtitle)) {
                return title + "\n" + subtitle;
            }
            return title;
        }
        return "等待播放";
    }

    private String firstLine(String value) {
        int index = value.indexOf('\n');
        if (index >= 0) {
            return value.substring(0, index).trim();
        }
        return value.trim();
    }

    private String cleanMusicText(String value) {
        if (value == null) {
            return "";
        }
        String text = value.replace('\r', '\n').trim();
        while (text.indexOf("\n\n") >= 0) {
            text = text.replace("\n\n", "\n");
        }
        return text;
    }

    private static boolean usefulNotifyLine(String text) {
        if (text == null) {
            return false;
        }
        String value = text.trim();
        if (value.length() == 0 || value.length() > 120) {
            return false;
        }
        String lower = value.toLowerCase(Locale.US);
        String[] blocked = {
                "android", "usb", "通知", "点击", "点按", "设置", "正在通过",
                "播放全部", "打开", "取消", "确定", "上一首", "下一首"
        };
        for (int i = 0; i < blocked.length; i++) {
            if (lower.indexOf(blocked[i].toLowerCase(Locale.US)) >= 0) {
                return false;
            }
        }
        return true;
    }

    private boolean sameText(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.trim().equalsIgnoreCase(b.trim());
    }

    private boolean isNotificationAccessEnabled() {
        try {
            String enabled = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
            if (enabled == null) {
                return false;
            }
            String mine = new ComponentName(this, ReviveNotificationListener.class).flattenToString();
            return enabled.indexOf(mine) >= 0;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isAccessibilityEnabled() {
        try {
            String enabled = Settings.Secure.getString(getContentResolver(), "enabled_accessibility_services");
            if (enabled == null) {
                return false;
            }
            String mine = new ComponentName(this, ReviveAccessibilityService.class).flattenToString();
            return enabled.indexOf(mine) >= 0;
        } catch (Exception ex) {
            return false;
        }
    }

    private void openNotificationAccessSettings() {
        ComponentName component = new ComponentName(this, ReviveNotificationListener.class);
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                Intent detail = new Intent("android.settings.NOTIFICATION_LISTENER_DETAIL_SETTINGS");
                detail.putExtra("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME",
                        component.flattenToString());
                startActivity(detail);
                return;
            } catch (Exception ignored) {
            }
        }
        try {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } catch (Exception ex) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void maybeShowNotificationAccessHint(boolean force) {
        if (Build.VERSION.SDK_INT < 21 || prefs == null || isNotificationAccessEnabled()) {
            return;
        }
        if (!force && prefs.getBoolean(PREF_NOTIFY_ACCESS_HINT_SHOWN, false)) {
            return;
        }
        if (notificationHintShowing || isFinishing()) {
            return;
        }
        notificationHintShowing = true;
        if (!force) {
            prefs.edit().putBoolean(PREF_NOTIFY_ACCESS_HINT_SHOWN, true).apply();
        }
        try {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("开启音乐信息读取")
                    .setMessage("要稳定读取 QQ音乐、酷狗、网易云等系统播放器小卡里的歌名和歌手，需要开启“通知读取”权限。Android 不会像定位那样弹出允许/拒绝窗口，只能到系统设置里打开。")
                    .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            notificationHintShowing = false;
                            openNotificationAccessSettings();
                        }
                    })
                    .setNegativeButton("稍后", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            notificationHintShowing = false;
                        }
                    })
                    .create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override public void onDismiss(DialogInterface dialog) {
                    notificationHintShowing = false;
                }
            });
            dialog.show();
        } catch (Exception ignored) {
            notificationHintShowing = false;
        }
    }

    private void enterImmersive() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void installImmersiveGuard() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0
                                || (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            restoreImmersiveSoon();
                        }
                    }
                });
    }

    private void restoreImmersiveSoon() {
        enterImmersive();
        handler.postDelayed(new Runnable() {
            @Override public void run() { enterImmersive(); }
        }, 160);
        handler.postDelayed(new Runnable() {
            @Override public void run() { enterImmersive(); }
        }, 520);
        handler.postDelayed(new Runnable() {
            @Override public void run() { enterImmersive(); }
        }, 1200);
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    private LinearLayout panel(String title, String subtitle, int accent) {
        boolean compact = compactLandscape();
        LinearLayout panel = column();
        panel.setPadding(compact ? dp(12) : dp(16), compact ? dp(9) : dp(14),
                compact ? dp(12) : dp(16), compact ? dp(9) : dp(14));
        panel.setBackground(panelBg(accent));
        if (Build.VERSION.SDK_INT >= 21) {
            panel.setElevation(dp(5));
        }
        TextView titleView = text(title, compact ? 18 : 21, Typeface.BOLD, TEXT);
        panel.addView(titleView, matchWrap());
        if (subtitle != null && subtitle.trim().length() > 0) {
            TextView subView = text(subtitle, compact ? 11 : 13, Typeface.NORMAL, MUTED);
            panel.addView(subView, matchWrap());
            addPanelGap(panel, compact ? 5 : 8);
        } else {
            addPanelGap(panel, compact ? 6 : 10);
        }
        return panel;
    }

    private LinearLayout metric(String top, String bottom, int color) {
        LinearLayout metric = column();
        metric.setPadding(dp(10), dp(8), dp(10), dp(8));
        metric.setBackground(metricBg(color));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) metric.getLayoutParams();
        TextView a = text(top, 14, Typeface.BOLD, color);
        TextView b = text(bottom, 12, Typeface.NORMAL, MUTED);
        metric.addView(a, matchWrap());
        metric.addView(b, matchWrap());
        return metric;
    }

    private TextView text(String value, float sp, int style, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTypeface(Typeface.DEFAULT, style);
        view.setTextColor(color);
        view.setIncludeFontPadding(true);
        allText.add(view);
        return view;
    }

    private TextView marqueeText(String value, float sp, int style, int color) {
        SeamlessMarqueeTextView view = new SeamlessMarqueeTextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTypeface(Typeface.DEFAULT, style);
        view.setTextColor(color);
        view.setSingleLine(true);
        view.setIncludeFontPadding(false);
        allText.add(view);
        return view;
    }

    private TextView flipDigit(String value, float sp) {
        FlipDigitView view = new FlipDigitView(this, CYAN);
        view.setText(value);
        view.setTextSize(sp);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(CYAN);
        view.setIncludeFontPadding(false);
        allText.add(view);
        return view;
    }

    private EditText editBox(String hint) {
        EditText edit = new EditText(this);
        edit.setGravity(Gravity.TOP | Gravity.LEFT);
        edit.setTextSize(17);
        edit.setTextColor(TEXT);
        edit.setHintTextColor(MUTED);
        edit.setHint(hint);
        edit.setMinLines(4);
        edit.setPadding(dp(12), dp(10), dp(12), dp(10));
        edit.setBackground(inputBg());
        allEdits.add(edit);
        return edit;
    }

    private EditText compactEdit(String hint) {
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setTextSize(compactLandscape() ? 12 : 14);
        edit.setTextColor(TEXT);
        edit.setHintTextColor(MUTED);
        edit.setHint(hint);
        edit.setPadding(compactLandscape() ? dp(7) : dp(9), 0, compactLandscape() ? dp(7) : dp(9), 0);
        edit.setBackground(inputBg());
        allEdits.add(edit);
        return edit;
    }

    private Button actionButton(String label, int color) {
        boolean compact = compactLandscape();
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(compact ? 13 : 16);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(Color.rgb(2, 12, 20));
        b.setMinHeight(compact ? dp(34) : dp(44));
        b.setPadding(compact ? dp(6) : dp(8), 0, compact ? dp(6) : dp(8), 0);
        b.setBackground(buttonBg(color, true));
        allButtons.add(b);
        return b;
    }

    private Button browserButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(getResources().getDisplayMetrics().widthPixels < dp(520) ? 13 : 15);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(Color.rgb(45, 55, 72));
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(4), 0, dp(4), 0);
        b.setMinHeight(0);
        b.setMinWidth(0);
        b.setBackground(browserButtonBg(Color.WHITE, Color.rgb(229, 235, 243),
                Color.rgb(247, 249, 252)));
        return b;
    }

    private Button browserAccentButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(getResources().getDisplayMetrics().widthPixels < dp(520) ? 13 : 15);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(Color.WHITE);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(4), 0, dp(4), 0);
        b.setMinHeight(0);
        b.setMinWidth(0);
        b.setBackground(browserButtonBg(Color.rgb(41, 197, 183),
                Color.rgb(34, 178, 166), Color.rgb(55, 213, 199)));
        return b;
    }

    private Button browserNavButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(20);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(Color.rgb(61, 73, 92));
        b.setGravity(Gravity.CENTER);
        b.setPadding(0, 0, 0, dp(1));
        b.setMinHeight(0);
        b.setMinWidth(0);
        b.setBackground(browserButtonBg(Color.WHITE, Color.rgb(226, 232, 240),
                Color.rgb(242, 246, 250)));
        return b;
    }

    private EditText browserEdit(String hint) {
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setTextSize(getResources().getDisplayMetrics().widthPixels < dp(520) ? 13 : 15);
        edit.setTextColor(Color.rgb(32, 41, 57));
        edit.setHintTextColor(Color.rgb(126, 139, 158));
        edit.setHint(hint);
        edit.setPadding(dp(14), 0, dp(14), 0);
        edit.setSelectAllOnFocus(false);
        edit.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        edit.setBackground(browserButtonBg(Color.WHITE, Color.rgb(226, 232, 240),
                Color.rgb(247, 250, 252)));
        return edit;
    }

    private LinearLayout.LayoutParams browserNavParams(boolean first) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
                getResources().getDisplayMetrics().widthPixels < dp(520) ? dp(44) : dp(48), 1f);
        p.setMargins(first ? 0 : dp(6), 0, 0, 0);
        return p;
    }

    private Button ghostButton(String label) {
        boolean compact = compactLandscape();
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(compact ? 13 : 15);
        b.setTextColor(TEXT);
        b.setMinHeight(compact ? dp(32) : dp(42));
        b.setPadding(compact ? dp(6) : dp(8), 0, compact ? dp(6) : dp(8), 0);
        b.setBackground(buttonBg(BLUE, false));
        allButtons.add(b);
        return b;
    }

    private Button tinyButton(String label, int color) {
        boolean compact = compactLandscape();
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(compact ? 12 : 14);
        b.setTextColor(Color.rgb(2, 12, 20));
        b.setPadding(compact ? dp(3) : dp(4), 0, compact ? dp(3) : dp(4), 0);
        b.setBackground(buttonBg(color, true));
        allButtons.add(b);
        return b;
    }

    private ToggleChip chip(String label, int color) {
        ToggleChip chip = new ToggleChip(this, color);
        chip.setText(label);
        chip.setGravity(Gravity.CENTER);
        chip.setTextSize(14);
        chip.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        chip.setPadding(dp(8), 0, dp(8), 0);
        return chip;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private void addGap(int value) {
        View gap = new View(this);
        mainLayout.addView(gap, new LinearLayout.LayoutParams(1, dp(value)));
    }

    private void addPanelGap(LinearLayout panel, int value) {
        View gap = new View(this);
        panel.addView(gap, new LinearLayout.LayoutParams(1, dp(value)));
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams weightedPanel(float weight) {
        return weightedPanel(weight, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams weightedPanel(float weight, int height) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, height, weight);
        p.setMargins(dp(5), 0, dp(5), 0);
        return p;
    }

    private LinearLayout.LayoutParams verticalPanel() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(10));
        return p;
    }

    private LinearLayout.LayoutParams chipParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(42), 1f);
        p.setMargins(dp(3), dp(6), dp(3), dp(2));
        return p;
    }

    private LinearLayout.LayoutParams dockChipParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(42), 1f);
        p.setMargins(dp(4), 0, dp(4), 0);
        return p;
    }

    private GradientDrawable panelBg(int accent) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { PANEL_2, PANEL });
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), blend(accent, Color.WHITE, 0.28f));
        return drawable;
    }

    private GradientDrawable dockBg() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.argb(236, 10, 16, 33), Color.argb(236, 17, 24, 49) });
        drawable.setCornerRadius(dp(22));
        drawable.setStroke(dp(1), Color.argb(90, 95, 130, 190));
        return drawable;
    }

    private GradientDrawable flipTileBg() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.rgb(19, 28, 55), Color.rgb(8, 12, 29) });
        drawable.setCornerRadius(dp(10));
        drawable.setStroke(dp(1), Color.argb(155, 70, 226, 210));
        return drawable;
    }

    private GradientDrawable inputBg() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.argb(210, 8, 12, 28), Color.argb(210, 18, 24, 44) });
        drawable.setCornerRadius(dp(12));
        drawable.setStroke(dp(1), Color.argb(90, 92, 120, 170));
        return drawable;
    }

    private GradientDrawable metricBg(int color) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { Color.argb(115, Color.red(color), Color.green(color), Color.blue(color)), Color.argb(32, 255, 255, 255) });
        drawable.setCornerRadius(dp(13));
        drawable.setStroke(dp(1), Color.argb(110, Color.red(color), Color.green(color), Color.blue(color)));
        return drawable;
    }

    private GradientDrawable buttonBg(int color, boolean filled) {
        GradientDrawable drawable;
        if (filled) {
            drawable = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[] { color, blend(color, Color.WHITE, 0.38f) });
            drawable.setStroke(dp(1), Color.argb(170, 255, 255, 255));
        } else {
            drawable = new GradientDrawable();
            drawable.setColor(Color.argb(80, 255, 255, 255));
            drawable.setStroke(dp(1), Color.argb(150, Color.red(color), Color.green(color), Color.blue(color)));
        }
        drawable.setCornerRadius(dp(22));
        return drawable;
    }

    private StateListDrawable browserButtonBg(int normal, int stroke, int pressed) {
        StateListDrawable list = new StateListDrawable();
        list.addState(new int[] { android.R.attr.state_pressed }, browserRoundBg(pressed, stroke));
        list.addState(new int[] { android.R.attr.state_focused }, browserRoundBg(pressed, stroke));
        list.addState(new int[] {}, browserRoundBg(normal, stroke));
        return list;
    }

    private GradientDrawable browserDialogBg() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(1), Color.rgb(226, 232, 240));
        return drawable;
    }

    private StateListDrawable browserTabRowBg(boolean active) {
        int normal = active ? Color.rgb(235, 252, 249) : Color.WHITE;
        int pressed = active ? Color.rgb(220, 247, 243) : Color.rgb(246, 249, 252);
        StateListDrawable list = new StateListDrawable();
        list.addState(new int[] { android.R.attr.state_pressed }, browserTabRoundBg(pressed));
        list.addState(new int[] {}, browserTabRoundBg(normal));
        return list;
    }

    private GradientDrawable browserTabRoundBg(int fill) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(12));
        return drawable;
    }

    private GradientDrawable browserRoundBg(int fill, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(24));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private GradientDrawable circleBg(int color, int fill) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(fill);
        drawable.setStroke(dp(2), color);
        return drawable;
    }

    private int blend(int a, int b, float amount) {
        int r = Math.round(Color.red(a) * (1f - amount) + Color.red(b) * amount);
        int g = Math.round(Color.green(a) * (1f - amount) + Color.green(b) * amount);
        int bl = Math.round(Color.blue(a) * (1f - amount) + Color.blue(b) * amount);
        return Color.rgb(r, g, bl);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String todayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static class ToggleChip extends TextView {
        private final int accent;
        private boolean checked;

        public ToggleChip(Context context, int accent) {
            super(context);
            this.accent = accent;
            setTextColor(MUTED);
            refresh();
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            refresh();
        }

        public void toggle() {
            checked = !checked;
            refresh();
        }

        private void refresh() {
            GradientDrawable bg;
            if (checked) {
                bg = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] { accent, Color.rgb(245, 250, 255) });
                setTextColor(Color.rgb(3, 14, 23));
            } else {
                bg = new GradientDrawable();
                bg.setColor(Color.argb(72, 255, 255, 255));
                bg.setStroke(dpLocal(1), Color.argb(125, Color.red(accent), Color.green(accent), Color.blue(accent)));
                setTextColor(Color.rgb(216, 226, 244));
            }
            bg.setCornerRadius(dpLocal(18));
            setBackground(bg);
        }

        private int dpLocal(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class AmbientBackgroundView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Rect src = new Rect();
        private final RectF dst = new RectF();
        private Bitmap bitmap;
        private int themeIndex;

        public AmbientBackgroundView(Context context) {
            super(context);
            linePaint.setStrokeWidth(2f);
            linePaint.setPathEffect(new DashPathEffect(new float[] { 12f, 18f }, 0f));
        }

        public void setTheme(int themeIndex, Bitmap bitmap) {
            this.themeIndex = themeIndex;
            this.bitmap = bitmap;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            if (bitmap != null && !bitmap.isRecycled()) {
                drawCoverBitmap(canvas, w, h);
                paint.setAlpha(255);
                paint.setShader(new LinearGradient(0, 0, 0, h,
                        Color.argb(72, 3, 7, 18),
                        Color.argb(154, 4, 8, 20),
                        Shader.TileMode.CLAMP));
                canvas.drawRect(0, 0, w, h, paint);
                paint.setShader(null);
            } else {
                paint.setAlpha(255);
                paint.setShader(new LinearGradient(0, 0, w, h, BG_TOP, BG_BOTTOM, Shader.TileMode.CLAMP));
                canvas.drawRect(0, 0, w, h, paint);
                paint.setShader(null);
            }

            boolean photo = bitmap != null && !bitmap.isRecycled();
            paint.setAlpha(255);
            paint.setColor(photo ? Color.argb(10, 64, 204, 255) : Color.argb(35, 64, 204, 255));
            for (int x = -w; x < w * 2; x += dpStatic(48)) {
                canvas.drawLine(x, 0, x + h, h, paint);
            }
            paint.setAlpha(255);
            paint.setColor(photo ? Color.argb(0, 255, 77, 166) : Color.argb(20, 255, 77, 166));
            for (int y = dpStatic(90); y < h; y += dpStatic(130)) {
                canvas.drawLine(0, y, w, y + dpStatic(34), paint);
            }

            if (!photo) {
                drawGlow(canvas, w, h, CYAN, h * 0.24f);
                drawGlow(canvas, w, h, PINK, h * 0.46f);
                drawGlow(canvas, w, h, BLUE, h * 0.68f);
            }

            paint.setAlpha(255);
            paint.setShader(new RadialGradient(w * 0.82f, h * 0.12f, Math.max(w, h) * 0.44f,
                    Color.argb(photo ? 42 : 72, 21, 226, 198), Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, w, h, paint);
            paint.setShader(null);
            paint.setAlpha(255);
        }

        private void drawCoverBitmap(Canvas canvas, int w, int h) {
            float bw = bitmap.getWidth();
            float bh = bitmap.getHeight();
            float viewRatio = w / (float) Math.max(1, h);
            float imageRatio = bw / Math.max(1f, bh);
            if (imageRatio > viewRatio) {
                float cropW = bh * viewRatio;
                float left = (bw - cropW) / 2f;
                src.set(Math.round(left), 0, Math.round(left + cropW), Math.round(bh));
            } else {
                float cropH = bw / Math.max(0.01f, viewRatio);
                float top = (bh - cropH) / 2f;
                src.set(0, Math.round(top), Math.round(bw), Math.round(top + cropH));
            }
            dst.set(0, 0, w, h);
            bitmapPaint.setAlpha(255);
            canvas.drawBitmap(bitmap, src, dst, bitmapPaint);
        }

        private void drawGlow(Canvas canvas, int w, int h, int color, float y) {
            linePaint.setShader(new LinearGradient(0, y, w, y + dpStatic(22),
                    Color.TRANSPARENT,
                    Color.argb(150, Color.red(color), Color.green(color), Color.blue(color)),
                    Shader.TileMode.CLAMP));
            linePaint.setStrokeWidth(dpStatic(3));
            canvas.drawLine(0, y, w, y + dpStatic(22), linePaint);
            linePaint.setShader(null);
        }

        private int dpStatic(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class WeatherIconView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int code = 2;

        public WeatherIconView(Context context) {
            super(context);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeCap(Paint.Cap.ROUND);
            stroke.setStrokeJoin(Paint.Join.ROUND);
        }

        public void setWeatherCode(int code) {
            this.code = code;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f;
            if (code == 0) {
                drawSun(canvas, cx, cy, Math.min(w, h) * 0.20f);
            } else if (code >= 51 && code <= 67 || code >= 80 && code <= 82) {
                drawCloud(canvas, cx, cy - h * 0.06f, Math.min(w, h) * 0.18f);
                drawRain(canvas, cx, cy + h * 0.14f);
            } else if (code >= 71 && code <= 77) {
                drawCloud(canvas, cx, cy - h * 0.06f, Math.min(w, h) * 0.18f);
                drawSnow(canvas, cx, cy + h * 0.14f);
            } else if (code >= 95) {
                drawCloud(canvas, cx, cy - h * 0.08f, Math.min(w, h) * 0.18f);
                drawLightning(canvas, cx, cy + h * 0.06f);
            } else if (code == 45 || code == 48) {
                drawFog(canvas, w, cy);
            } else {
                drawCloud(canvas, cx, cy, Math.min(w, h) * 0.20f);
            }
        }

        private void drawSun(Canvas canvas, float cx, float cy, float r) {
            paint.setColor(CYAN);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, r, paint);
            stroke.setColor(Color.argb(220, 255, 255, 255));
            stroke.setStrokeWidth(dpLocal(2));
            for (int i = 0; i < 8; i++) {
                double a = Math.toRadians(i * 45);
                canvas.drawLine(cx + (float) Math.cos(a) * r * 1.45f, cy + (float) Math.sin(a) * r * 1.45f,
                        cx + (float) Math.cos(a) * r * 1.95f, cy + (float) Math.sin(a) * r * 1.95f, stroke);
            }
        }

        private void drawCloud(Canvas canvas, float cx, float cy, float r) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(228, 245, 255));
            canvas.drawCircle(cx - r * 1.0f, cy + r * 0.15f, r * 0.86f, paint);
            canvas.drawCircle(cx - r * 0.25f, cy - r * 0.35f, r * 1.05f, paint);
            canvas.drawCircle(cx + r * 0.85f, cy + r * 0.10f, r * 0.78f, paint);
            RectF base = new RectF(cx - r * 1.68f, cy + r * 0.18f, cx + r * 1.58f, cy + r * 0.95f);
            canvas.drawRoundRect(base, r * 0.38f, r * 0.38f, paint);
        }

        private void drawRain(Canvas canvas, float cx, float y) {
            stroke.setColor(BLUE);
            stroke.setStrokeWidth(dpLocal(2));
            for (int i = -1; i <= 1; i++) {
                canvas.drawLine(cx + i * dpLocal(9), y, cx + i * dpLocal(9) - dpLocal(4), y + dpLocal(12), stroke);
            }
        }

        private void drawSnow(Canvas canvas, float cx, float y) {
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(dpLocal(16));
            canvas.drawText("*", cx - dpLocal(10), y + dpLocal(7), paint);
            canvas.drawText("*", cx + dpLocal(10), y + dpLocal(10), paint);
        }

        private void drawLightning(Canvas canvas, float cx, float y) {
            Path bolt = new Path();
            bolt.moveTo(cx - dpLocal(4), y - dpLocal(4));
            bolt.lineTo(cx + dpLocal(5), y - dpLocal(4));
            bolt.lineTo(cx, y + dpLocal(7));
            bolt.lineTo(cx + dpLocal(9), y + dpLocal(7));
            bolt.lineTo(cx - dpLocal(4), y + dpLocal(24));
            bolt.lineTo(cx, y + dpLocal(10));
            bolt.lineTo(cx - dpLocal(8), y + dpLocal(10));
            bolt.close();
            paint.setColor(AMBER);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(bolt, paint);
        }

        private void drawFog(Canvas canvas, int w, float cy) {
            stroke.setColor(Color.rgb(228, 245, 255));
            stroke.setStrokeWidth(dpLocal(3));
            for (int i = 0; i < 3; i++) {
                float y = cy - dpLocal(10) + i * dpLocal(10);
                canvas.drawLine(w * 0.25f, y, w * 0.75f, y, stroke);
            }
        }

        private int dpLocal(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class FlipDigitView extends TextView {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private final int accent;
        private String previousText = "";
        private float fold = 0f;
        private boolean folding;

        public FlipDigitView(Context context, int accent) {
            super(context);
            this.accent = accent;
            setWillNotDraw(false);
        }

        public void prepareFlip(String oldText) {
            previousText = oldText == null ? "" : oldText;
        }

        public void flipFromCenter() {
            if (previousText.length() == 0) {
                previousText = getText() == null ? "" : getText().toString();
            }
            folding = true;
            fold = 1f;
            animate().cancel();
            final long start = SystemClock.uptimeMillis();
            final Runnable step = new Runnable() {
                @Override public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.min(1f, elapsed / 210f);
                    fold = 1f - t;
                    invalidate();
                    if (t < 1f && folding) {
                        postDelayed(this, 16);
                    } else {
                        folding = false;
                        fold = 0f;
                        previousText = "";
                        invalidate();
                    }
                }
            };
            post(step);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();
            paint.reset();
            paint.setAntiAlias(true);
            paint.setAlpha(255);
            rect.set(0, 0, w, h);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, h,
                    Color.rgb(11, 18, 39), Color.rgb(7, 12, 29), Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, dpLocal(10), dpLocal(10), paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dpLocal(1));
            paint.setColor(Color.argb(155, Color.red(accent), Color.green(accent), Color.blue(accent)));
            canvas.drawRoundRect(rect, dpLocal(10), dpLocal(10), paint);
            paint.setColor(Color.argb(95, 255, 255, 255));
            canvas.drawLine(dpLocal(8), h / 2f, w - dpLocal(8), h / 2f, paint);
            drawDigitText(canvas, getText() == null ? "" : getText().toString(), w, h, accent);
            if (folding && previousText.length() > 0 && fold > 0f) {
                drawFoldingTop(canvas, w, h);
            }
        }

        private void drawFoldingTop(Canvas canvas, int w, int h) {
            int save = canvas.save();
            canvas.clipRect(0, 0, w, h / 2f);
            canvas.translate(0, h / 2f);
            canvas.scale(1f, Math.max(0.03f, fold), w / 2f, 0);
            canvas.translate(0, -h / 2f);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, h / 2f,
                    Color.rgb(16, 25, 52), Color.rgb(6, 10, 24), Shader.TileMode.CLAMP));
            canvas.drawRoundRect(new RectF(0, 0, w, h), dpLocal(10), dpLocal(10), paint);
            paint.setShader(null);
            drawDigitText(canvas, previousText, w, h,
                    Color.argb(235, Color.red(accent), Color.green(accent), Color.blue(accent)));
            canvas.restoreToCount(save);
        }

        private void drawDigitText(Canvas canvas, String value, int w, int h, int color) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(255);
            paint.setColor(color);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(getTypeface());
            paint.setTextSize(getTextSize());
            Paint.FontMetrics fm = paint.getFontMetrics();
            float baseline = h / 2f - (fm.ascent + fm.descent) / 2f;
            canvas.drawText(value == null ? "" : value, w / 2f, baseline, paint);
        }

        private int dpLocal(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class AlbumArtView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private final Rect src = new Rect();
        private Bitmap bitmap;

        public AlbumArtView(Context context) {
            super(context);
        }

        public void setAlbumArt(Bitmap bitmap) {
            this.bitmap = bitmap;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            rect.set(0, 0, w, h);
            if (bitmap != null && !bitmap.isRecycled()) {
                paint.reset();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);
                paint.setAlpha(255);
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(null);
                float bw = bitmap.getWidth();
                float bh = bitmap.getHeight();
                float side = Math.min(bw, bh);
                src.set(Math.round((bw - side) / 2f), Math.round((bh - side) / 2f),
                        Math.round((bw + side) / 2f), Math.round((bh + side) / 2f));
                canvas.drawBitmap(bitmap, src, rect, paint);
            } else {
                paint.reset();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(new LinearGradient(0, 0, w, h,
                        new int[] { PINK, BLUE, CYAN },
                        null,
                        Shader.TileMode.CLAMP));
                canvas.drawRoundRect(rect, dpLocal(8), dpLocal(8), paint);
                paint.setShader(null);
                paint.setColor(Color.argb(80, 0, 0, 0));
                canvas.drawCircle(w * 0.50f, h * 0.50f, Math.min(w, h) * 0.34f, paint);
                paint.setColor(Color.argb(225, 255, 255, 255));
                canvas.drawCircle(w * 0.50f, h * 0.50f, Math.min(w, h) * 0.10f, paint);
                paint.setColor(Color.argb(130, 255, 255, 255));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextSize(dpLocal(15));
                canvas.drawText("K", w * 0.50f, h * 0.55f, paint);
            }
        }

        private int dpLocal(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class SnapshotPreviewView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        private final Rect src = new Rect();
        private final Rect dst = new Rect();
        private final Bitmap bitmap;

        public SnapshotPreviewView(Context context, Bitmap bitmap) {
            super(context);
            this.bitmap = bitmap;
            paint.setDither(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            canvas.drawColor(Color.WHITE);
            if (bitmap == null || bitmap.isRecycled() || w <= 0 || h <= 0) {
                return;
            }
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            if (bw <= 0 || bh <= 0) {
                return;
            }

            float cardRatio = w / (float) h;
            float bitmapRatio = bw / (float) bh;
            if (bitmapRatio > cardRatio) {
                int cropW = Math.max(1, Math.round(bh * cardRatio));
                int left = Math.max(0, (bw - cropW) / 2);
                src.set(left, 0, Math.min(bw, left + cropW), bh);
            } else {
                int cropH = Math.max(1, Math.round(bw / cardRatio));
                int top = Math.max(0, (bh - cropH) / 2);
                src.set(0, top, bw, Math.min(bh, top + cropH));
            }
            dst.set(0, 0, w, h);
            canvas.drawBitmap(bitmap, src, dst, paint);
        }
    }

    public static class SeamlessMarqueeTextView extends TextView {
        private final Paint marqueePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private long startAt;
        private float textWidth;
        private float gap;
        private String value = "";
        private boolean scrolling;

        public SeamlessMarqueeTextView(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        public void setText(CharSequence text, BufferType type) {
            String next = text == null ? "" : text.toString();
            boolean changed = !next.equals(value);
            super.setText(text, type);
            value = next;
            if (changed) {
                startAt = SystemClock.uptimeMillis();
                textWidth = 0f;
                scrolling = false;
            }
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            String text = value == null ? "" : value;
            if (text.length() == 0) {
                return;
            }
            marqueePaint.set(getPaint());
            marqueePaint.setColor(getCurrentTextColor());
            textWidth = marqueePaint.measureText(text);
            gap = Math.max(dpLocal(34), getWidth() * 0.26f);
            Paint.FontMetrics fm = marqueePaint.getFontMetrics();
            float baseline = getHeight() / 2f - (fm.ascent + fm.descent) / 2f;
            if (textWidth <= getWidth()) {
                canvas.drawText(text, 0, baseline, marqueePaint);
                scrolling = false;
                return;
            }
            scrolling = true;
            float distance = textWidth + gap;
            float speed = dpLocal(32) / 1000f;
            float offset = ((SystemClock.uptimeMillis() - startAt) * speed) % distance;
            float x = -offset;
            while (x > 0) {
                x -= distance;
            }
            while (x < getWidth()) {
                canvas.drawText(text, x, baseline, marqueePaint);
                x += distance;
            }
            postInvalidateDelayed(16);
        }

        @Override
        protected void onDetachedFromWindow() {
            scrolling = false;
            super.onDetachedFromWindow();
        }

        private int dpLocal(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class TodoCheckView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean checked;

        public TodoCheckView(Context context) {
            super(context);
            setClickable(false);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f;
            float r = Math.min(w, h) * 0.38f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dpLocal(2));
            paint.setColor(checked ? GREEN : Color.argb(180, 185, 199, 222));
            canvas.drawCircle(cx, cy, r, paint);
            if (checked) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(GREEN);
                canvas.drawCircle(cx, cy, r * 0.82f, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dpLocal(3));
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setColor(Color.rgb(3, 14, 23));
                Path path = new Path();
                path.moveTo(cx - r * 0.42f, cy - r * 0.02f);
                path.lineTo(cx - r * 0.12f, cy + r * 0.30f);
                path.lineTo(cx + r * 0.48f, cy - r * 0.34f);
                canvas.drawPath(path, paint);
            }
        }

        private int dpLocal(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    public static class IconButton extends View {
        public static final int PREVIOUS = 0;
        public static final int PLAY = 1;
        public static final int NEXT = 2;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int type;
        private final int accent;
        private boolean playing;

        public IconButton(Context context, int type, int accent) {
            super(context);
            this.type = type;
            this.accent = accent;
            setClickable(true);
        }

        public void setPlaying(boolean playing) {
            this.playing = playing;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            float r = Math.min(w, h) * 0.48f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(92, Color.red(accent), Color.green(accent), Color.blue(accent)));
            canvas.drawCircle(w / 2f, h / 2f, r, paint);
            paint.setColor(Color.rgb(238, 248, 255));
            if (type == PLAY) {
                if (playing) {
                    canvas.drawRoundRect(new RectF(w * 0.38f, h * 0.32f, w * 0.48f, h * 0.68f),
                            w * 0.03f, w * 0.03f, paint);
                    canvas.drawRoundRect(new RectF(w * 0.56f, h * 0.32f, w * 0.66f, h * 0.68f),
                            w * 0.03f, w * 0.03f, paint);
                } else {
                    Path tri = new Path();
                    tri.moveTo(w * 0.42f, h * 0.32f);
                    tri.lineTo(w * 0.42f, h * 0.68f);
                    tri.lineTo(w * 0.70f, h * 0.50f);
                    tri.close();
                    canvas.drawPath(tri, paint);
                }
            } else {
                float dir = type == PREVIOUS ? -1f : 1f;
                drawTriangle(canvas, w / 2f + dir * w * 0.02f, h * 0.50f, dir);
                drawTriangle(canvas, w / 2f + dir * w * 0.18f, h * 0.50f, dir);
                paint.setStrokeWidth(Math.max(2f, w * 0.05f));
                canvas.drawLine(w / 2f - dir * w * 0.22f, h * 0.34f,
                        w / 2f - dir * w * 0.22f, h * 0.66f, paint);
            }
        }

        private void drawTriangle(Canvas canvas, float cx, float cy, float dir) {
            float w = getWidth();
            float h = getHeight();
            Path tri = new Path();
            tri.moveTo(cx + dir * w * 0.10f, cy);
            tri.lineTo(cx - dir * w * 0.10f, cy - h * 0.16f);
            tri.lineTo(cx - dir * w * 0.10f, cy + h * 0.16f);
            tri.close();
            canvas.drawPath(tri, paint);
        }
    }

    public static class TomatoDialView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF arc = new RectF();
        private long remainingMillis = 25L * 60L * 1000L;
        private long totalMillis = 25L * 60L * 1000L;
        private String label = "专注";
        private boolean running;

        public TomatoDialView(Context context) {
            super(context);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeCap(Paint.Cap.ROUND);
        }

        public void setTimerState(long remainingMillis, long totalMillis, String label, boolean running) {
            this.remainingMillis = remainingMillis;
            this.totalMillis = totalMillis <= 0 ? 1 : totalMillis;
            this.label = label;
            this.running = running;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f + dp(6);
            float radius = Math.min(w, h) * 0.35f;

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new RadialGradient(cx, cy, radius * 1.2f,
                    Color.argb(130, 255, 77, 166), Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawCircle(cx, cy, radius * 1.45f, paint);
            paint.setShader(null);

            paint.setShader(new RadialGradient(cx - radius * 0.24f, cy - radius * 0.32f, radius * 1.22f,
                    Color.rgb(255, 122, 131), Color.rgb(218, 36, 66), Shader.TileMode.CLAMP));
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setShader(null);

            Path leaf = new Path();
            leaf.moveTo(cx, cy - radius * 0.92f);
            leaf.cubicTo(cx - radius * 0.32f, cy - radius * 1.18f, cx - radius * 0.06f, cy - radius * 1.33f, cx, cy - radius * 1.05f);
            leaf.cubicTo(cx + radius * 0.08f, cy - radius * 1.34f, cx + radius * 0.36f, cy - radius * 1.15f, cx + radius * 0.10f, cy - radius * 0.90f);
            paint.setColor(Color.rgb(38, 219, 133));
            canvas.drawPath(leaf, paint);

            stroke.setStrokeWidth(dp(11));
            stroke.setColor(Color.argb(82, 255, 255, 255));
            arc.set(cx - radius * 1.16f, cy - radius * 1.16f, cx + radius * 1.16f, cy + radius * 1.16f);
            canvas.drawArc(arc, -90, 360, false, stroke);

            float progress = 1f - Math.max(0f, Math.min(1f, remainingMillis / (float) totalMillis));
            stroke.setShader(new SweepGradient(cx, cy, new int[] { CYAN, AMBER, PINK, CYAN }, null));
            stroke.setColor(CYAN);
            canvas.drawArc(arc, -90, Math.max(2f, 360f * progress), false, stroke);
            stroke.setShader(null);

            stroke.setStrokeWidth(dp(2));
            stroke.setColor(Color.argb(95, 255, 255, 255));
            for (int i = 0; i < 12; i++) {
                double angle = Math.toRadians(i * 30 - 90);
                float sx = cx + (float) Math.cos(angle) * radius * 1.02f;
                float sy = cy + (float) Math.sin(angle) * radius * 1.02f;
                float ex = cx + (float) Math.cos(angle) * radius * 1.13f;
                float ey = cy + (float) Math.sin(angle) * radius * 1.13f;
                canvas.drawLine(sx, sy, ex, ey, stroke);
            }

            long totalSeconds = Math.max(0, (remainingMillis + 999) / 1000);
            String time = String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60);
            paint.setShader(null);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextSize(dp(34));
            paint.setColor(Color.WHITE);
            canvas.drawText(time, cx, cy + dp(10), paint);

            paint.setTextSize(dp(13));
            paint.setColor(Color.argb(235, 255, 255, 255));
            canvas.drawText(running ? label + "中" : label + "待命", cx, cy - dp(28), paint);
        }

        private int dp(int value) {
            return Math.round(value * getResources().getDisplayMetrics().density);
        }
    }

    private static class CityCandidate {
        final String name;
        final String label;
        final String alias;
        final double lat;
        final double lon;

        CityCandidate(String name, String label, double lat, double lon) {
            this(name, label, "", lat, lon);
        }

        CityCandidate(String name, String label, String alias, double lat, double lon) {
            this.name = name;
            this.label = label;
            this.alias = alias;
            this.lat = lat;
            this.lon = lon;
        }
    }

    private static final CityCandidate[] LOCAL_CITIES = new CityCandidate[] {
            new CityCandidate("北京", "北京 · 北京市", "beijing 北平", 39.9042, 116.4074),
            new CityCandidate("北海", "北海 · 广西", "beihai", 21.4811, 109.1201),
            new CityCandidate("北流", "北流 · 广西", "beiliu", 22.7082, 110.3540),
            new CityCandidate("福州", "福州 · 福建", "fuzhou", 26.0745, 119.2965),
            new CityCandidate("泉州", "泉州 · 福建", "quanzhou 刺桐", 24.8741, 118.6757),
            new CityCandidate("漳州", "漳州 · 福建", "zhangzhou", 24.5130, 117.6471),
            new CityCandidate("莆田", "莆田 · 福建", "putian", 25.4540, 119.0078),
            new CityCandidate("宁德", "宁德 · 福建", "ningde", 26.6657, 119.5482),
            new CityCandidate("三明", "三明 · 福建", "sanming", 26.2634, 117.6387),
            new CityCandidate("南平", "南平 · 福建", "nanping", 26.6418, 118.1774),
            new CityCandidate("龙岩", "龙岩 · 福建", "longyan", 25.0751, 117.0175),
            new CityCandidate("上海", "上海 · 上海市", "shanghai", 31.2304, 121.4737),
            new CityCandidate("广州", "广州 · 广东", "guangzhou", 23.1291, 113.2644),
            new CityCandidate("深圳", "深圳 · 广东", "shenzhen", 22.5431, 114.0579),
            new CityCandidate("杭州", "杭州 · 浙江", "hangzhou", 30.2741, 120.1551),
            new CityCandidate("厦门", "厦门 · 福建", "xiamen", 24.4798, 118.0894),
            new CityCandidate("南京", "南京 · 江苏", "nanjing", 32.0603, 118.7969),
            new CityCandidate("苏州", "苏州 · 江苏", "suzhou", 31.2989, 120.5853),
            new CityCandidate("成都", "成都 · 四川", "chengdu", 30.5728, 104.0668),
            new CityCandidate("重庆", "重庆 · 重庆市", "chongqing", 29.5630, 106.5516),
            new CityCandidate("武汉", "武汉 · 湖北", "wuhan", 30.5928, 114.3055),
            new CityCandidate("西安", "西安 · 陕西", "xian", 34.3416, 108.9398),
            new CityCandidate("天津", "天津 · 天津市", "tianjin", 39.3434, 117.3616),
            new CityCandidate("青岛", "青岛 · 山东", "qingdao", 36.0671, 120.3826),
            new CityCandidate("济南", "济南 · 山东", "jinan", 36.6512, 117.1201),
            new CityCandidate("郑州", "郑州 · 河南", "zhengzhou", 34.7466, 113.6254),
            new CityCandidate("长沙", "长沙 · 湖南", "changsha", 28.2282, 112.9388),
            new CityCandidate("南昌", "南昌 · 江西", "nanchang", 28.6820, 115.8582),
            new CityCandidate("合肥", "合肥 · 安徽", "hefei", 31.8206, 117.2272),
            new CityCandidate("昆明", "昆明 · 云南", "kunming", 25.0389, 102.7183),
            new CityCandidate("贵阳", "贵阳 · 贵州", "guiyang", 26.6470, 106.6302),
            new CityCandidate("南宁", "南宁 · 广西", "nanning", 22.8170, 108.3669),
            new CityCandidate("海口", "海口 · 海南", "haikou", 20.0440, 110.1999),
            new CityCandidate("三亚", "三亚 · 海南", "sanya", 18.2528, 109.5119),
            new CityCandidate("沈阳", "沈阳 · 辽宁", "shenyang", 41.8057, 123.4315),
            new CityCandidate("大连", "大连 · 辽宁", "dalian", 38.9140, 121.6147),
            new CityCandidate("哈尔滨", "哈尔滨 · 黑龙江", "harbin", 45.8038, 126.5349),
            new CityCandidate("长春", "长春 · 吉林", "changchun", 43.8171, 125.3235),
            new CityCandidate("石家庄", "石家庄 · 河北", "shijiazhuang", 38.0428, 114.5149),
            new CityCandidate("太原", "太原 · 山西", "taiyuan", 37.8706, 112.5489),
            new CityCandidate("呼和浩特", "呼和浩特 · 内蒙古", "hohhot", 40.8426, 111.7492),
            new CityCandidate("兰州", "兰州 · 甘肃", "lanzhou", 36.0611, 103.8343),
            new CityCandidate("银川", "银川 · 宁夏", "yinchuan", 38.4872, 106.2309),
            new CityCandidate("西宁", "西宁 · 青海", "xining", 36.6171, 101.7782),
            new CityCandidate("乌鲁木齐", "乌鲁木齐 · 新疆", "urumqi", 43.8256, 87.6168),
            new CityCandidate("拉萨", "拉萨 · 西藏", "lhasa", 29.6520, 91.1721),
            new CityCandidate("香港", "香港", "hongkong", 22.3193, 114.1694),
            new CityCandidate("澳门", "澳门", "macau", 22.1987, 113.5439),
            new CityCandidate("台北", "台北 · 台湾", "taipei", 25.0330, 121.5654),
            new CityCandidate("纽约", "纽约 · 美国", "New York nyc", 40.7143, -74.0060),
            new CityCandidate("伦敦", "伦敦 · 英国", "London", 51.5072, -0.1276),
            new CityCandidate("巴黎", "巴黎 · 法国", "Paris", 48.8566, 2.3522),
            new CityCandidate("东京", "东京 · 日本", "Tokyo", 35.6764, 139.6500),
            new CityCandidate("洛杉矶", "洛杉矶 · 美国", "Los Angeles la", 34.0522, -118.2437),
            new CityCandidate("旧金山", "旧金山 · 美国", "San Francisco", 37.7749, -122.4194),
            new CityCandidate("新加坡", "新加坡", "Singapore", 1.3521, 103.8198),
            new CityCandidate("首尔", "首尔 · 韩国", "Seoul", 37.5665, 126.9780),
            new CityCandidate("曼谷", "曼谷 · 泰国", "Bangkok", 13.7563, 100.5018),
            new CityCandidate("悉尼", "悉尼 · 澳大利亚", "Sydney", -33.8688, 151.2093)
    };

    public static class ReviveNotificationListener extends NotificationListenerService {
        @Override
        public void onListenerConnected() {
            try {
                StatusBarNotification[] active = getActiveNotifications();
                if (active == null) {
                    return;
                }
                for (int i = 0; i < active.length; i++) {
                    onNotificationPosted(active[i]);
                }
            } catch (Exception ignored) {
            }
        }

        @Override
        public void onNotificationPosted(StatusBarNotification sbn) {
            if (sbn == null) {
                return;
            }
            try {
                Notification notification = sbn.getNotification();
                if (notification == null || notification.extras == null) {
                    return;
                }
                if (!looksLikeMediaNotification(sbn, notification)) {
                    return;
                }
                latestNotifyPackage = sbn.getPackageName();
                latestNotifyTitle = "";
                latestNotifyArtist = "";
                latestNotifyLyric = "";
                latestNotifyText = "";
                CharSequence title = firstText(
                        notification.extras.getCharSequence(Notification.EXTRA_TITLE),
                        notification.extras.getCharSequence("android.title.big"),
                        notification.extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE));
                if (title != null && title.length() > 0) {
                    latestNotifyTitle = title.toString();
                }
                latestNotifyText = bestNotifyText(notification);
                parseTicker(notification);
                parseNotifyTextParts();
                latestNotifyPlayback = playbackFromActions(notification);
                Bitmap large = bitmapExtra(notification, Notification.EXTRA_LARGE_ICON, this);
                if (large == null && Build.VERSION.SDK_INT >= 23) {
                    large = bitmapFromIcon(notification.getLargeIcon(), this);
                }
                if (large == null) {
                    large = notification.largeIcon;
                }
                if (large != null) {
                    latestNotifyArt = large;
                    latestNotifyArtAt = System.currentTimeMillis();
                }
                latestNotifyAt = System.currentTimeMillis();
            } catch (Exception ignored) {
            }
        }

        private static String bestNotifyText(Notification notification) {
            String[] values = new String[] {
                    textExtra(notification, Notification.EXTRA_TEXT),
                    textExtra(notification, Notification.EXTRA_SUB_TEXT),
                    textExtra(notification, "android.textLines"),
                    textExtra(notification, "android.title.big"),
                    textExtra(notification, Notification.EXTRA_BIG_TEXT),
                    textExtra(notification, Notification.EXTRA_SUMMARY_TEXT),
                    textExtra(notification, "android.infoText"),
                    textLinesExtra(notification),
                    remoteViewsText(notification.contentView),
                    remoteViewsText(notification.bigContentView),
                    remoteViewsText(notification.headsUpContentView)
            };
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (value == null) {
                    continue;
                }
                value = value.trim();
                if (value.length() == 0 || isPlayerName(value)) {
                    continue;
                }
                if (builder.indexOf(value) >= 0) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(value);
            }
            return builder.toString();
        }

        private static void parseTicker(Notification notification) {
            try {
                if (notification.tickerText == null) {
                    return;
                }
                String ticker = notification.tickerText.toString().trim();
                int split = ticker.indexOf(" - ");
                if (split <= 0 || split >= ticker.length() - 3) {
                    return;
                }
                String artist = ticker.substring(0, split).trim();
                String title = ticker.substring(split + 3).trim();
                if (title.length() > 0) {
                    latestNotifyTitle = title;
                }
                if (artist.length() > 0) {
                    latestNotifyArtist = artist;
                }
            } catch (Exception ignored) {
            }
        }

        private static void parseNotifyTextParts() {
            try {
                String text = latestNotifyText == null ? "" : latestNotifyText.trim();
                if (text.length() == 0) {
                    return;
                }
                String[] lines = text.split("\\n+");
                StringBuilder remain = new StringBuilder();
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i] == null ? "" : lines[i].trim();
                    if (!usefulNotifyLine(line)) {
                        continue;
                    }
                    int split = line.indexOf(" - ");
                    if (split > 0 && split < line.length() - 3) {
                        String artist = line.substring(0, split).trim();
                        String title = line.substring(split + 3).trim();
                        if (latestNotifyArtist.length() == 0 && artist.length() > 0) {
                            latestNotifyArtist = artist;
                        }
                        if (latestNotifyTitle.length() == 0 && title.length() > 0) {
                            latestNotifyTitle = title;
                        }
                        continue;
                    }
                    if (sameStatic(line, latestNotifyTitle) || sameStatic(line, latestNotifyArtist)) {
                        continue;
                    }
                    if (line.indexOf("酷狗") >= 0 || line.toLowerCase(Locale.US).indexOf("music") >= 0) {
                        continue;
                    }
                    if (remain.length() > 0) {
                        remain.append('\n');
                    }
                    remain.append(line);
                }
                if (remain.length() > 0) {
                    latestNotifyLyric = remain.toString();
                    latestLyricText = latestNotifyLyric;
                    latestLyricAt = System.currentTimeMillis();
                }
            } catch (Exception ignored) {
            }
        }

        private static boolean sameStatic(String a, String b) {
            if (a == null || b == null) {
                return false;
            }
            return a.trim().equalsIgnoreCase(b.trim());
        }

        private static boolean looksLikeMediaNotification(StatusBarNotification sbn, Notification notification) {
            try {
                if (isSystemNotificationPackage(sbn.getPackageName())) {
                    return notification.extras.getParcelable(Notification.EXTRA_MEDIA_SESSION) != null;
                }
                String ticker = notification.tickerText == null ? "" : notification.tickerText.toString().trim();
                String lowerTicker = ticker.toLowerCase(Locale.US);
                if (ticker.indexOf("USB") >= 0 || ticker.indexOf("调试") >= 0
                        || ticker.indexOf("传输文件") >= 0 || lowerTicker.indexOf("usb") >= 0) {
                    return false;
                }
                if (notification.extras.getParcelable(Notification.EXTRA_MEDIA_SESSION) != null) {
                    return true;
                }
                if (notification.actions != null && playbackFromActions(notification) >= 0) {
                    return true;
                }
                String pkg = sbn.getPackageName();
                String text = (String.valueOf(notification.tickerText) + "\n"
                        + bestPlainNotifyText(notification)).toLowerCase(Locale.US);
                if (isKnownMusicPackage(pkg)) {
                    return true;
                }
                return text.indexOf("播放") >= 0 || text.indexOf("暂停") >= 0
                        || text.indexOf("上一首") >= 0 || text.indexOf("下一首") >= 0
                        || ticker.indexOf(" - ") > 0;
            } catch (Exception ex) {
                return false;
            }
        }

        private static String textExtra(Notification notification, String key) {
            try {
                CharSequence value = notification.extras.getCharSequence(key);
                return value == null ? "" : value.toString();
            } catch (Exception ex) {
                return "";
            }
        }

        private static String textLinesExtra(Notification notification) {
            try {
                CharSequence[] lines = notification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
                if (lines == null || lines.length == 0) {
                    return "";
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i] == null) {
                        continue;
                    }
                    String value = lines[i].toString().trim();
                    if (value.length() == 0) {
                        continue;
                    }
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(value);
                }
                return builder.toString();
            } catch (Exception ex) {
                return "";
            }
        }

        private static String bestPlainNotifyText(Notification notification) {
            StringBuilder builder = new StringBuilder();
            String[] values = new String[] {
                    textExtra(notification, Notification.EXTRA_TITLE),
                    textExtra(notification, Notification.EXTRA_TEXT),
                    textExtra(notification, Notification.EXTRA_SUB_TEXT),
                    textExtra(notification, "android.title.big"),
                    textExtra(notification, Notification.EXTRA_BIG_TEXT),
                    textExtra(notification, Notification.EXTRA_SUMMARY_TEXT),
                    textLinesExtra(notification)
            };
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null || values[i].trim().length() == 0) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(values[i].trim());
            }
            return builder.toString();
        }

        private static CharSequence firstText(CharSequence... values) {
            if (values == null) {
                return null;
            }
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null && values[i].toString().trim().length() > 0) {
                    return values[i];
                }
            }
            return null;
        }

        private static boolean isPlayerName(String value) {
            if (value == null) {
                return true;
            }
            String text = value.trim();
            if (text.length() == 0) {
                return true;
            }
            String lower = text.toLowerCase(Locale.US);
            return text.equals("酷狗音乐")
                    || text.equals("QQ音乐")
                    || text.equals("网易云音乐")
                    || text.equals("酷我音乐")
                    || lower.equals("qq music")
                    || lower.equals("music");
        }

        private static String remoteViewsText(RemoteViews views) {
            if (views == null) {
                return "";
            }
            try {
                Field actionsField = RemoteViews.class.getDeclaredField("mActions");
                actionsField.setAccessible(true);
                Object actions = actionsField.get(views);
                if (!(actions instanceof ArrayList)) {
                    return "";
                }
                StringBuilder builder = new StringBuilder();
                ArrayList list = (ArrayList) actions;
                for (int i = 0; i < list.size(); i++) {
                    Object action = list.get(i);
                    String value = textFromRemoteAction(action);
                    if (value.length() == 0 || builder.indexOf(value) >= 0) {
                        continue;
                    }
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(value);
                }
                return builder.toString();
            } catch (Exception ex) {
                return "";
            }
        }

        private static String textFromRemoteAction(Object action) {
            if (action == null) {
                return "";
            }
            try {
                Field methodField = findField(action.getClass(), "methodName");
                Field valueField = findField(action.getClass(), "value");
                if (methodField == null || valueField == null) {
                    return "";
                }
                methodField.setAccessible(true);
                valueField.setAccessible(true);
                Object method = methodField.get(action);
                Object value = valueField.get(action);
                if (method == null || value == null) {
                    return "";
                }
                String methodName = method.toString();
                if (methodName.indexOf("setText") < 0 && methodName.indexOf("setContentDescription") < 0) {
                    return "";
                }
                String text = value.toString().trim();
                return usefulNotifyLine(text) ? text : "";
            } catch (Exception ex) {
                return "";
            }
        }

        private static Field findField(Class cls, String name) {
            Class current = cls;
            while (current != null) {
                try {
                    return current.getDeclaredField(name);
                } catch (Exception ignored) {
                    current = current.getSuperclass();
                }
            }
            return null;
        }

        private static int playbackFromActions(Notification notification) {
            try {
                if (notification.actions == null) {
                    return -1;
                }
                for (int i = 0; i < notification.actions.length; i++) {
                    Notification.Action action = notification.actions[i];
                    if (action == null || action.title == null) {
                        continue;
                    }
                    String label = action.title.toString();
                    if (label.indexOf("暂停") >= 0 || label.toLowerCase(Locale.US).indexOf("pause") >= 0) {
                        return 1;
                    }
                    if (label.indexOf("播放") >= 0 || label.toLowerCase(Locale.US).indexOf("play") >= 0) {
                        return 0;
                    }
                }
            } catch (Exception ignored) {
            }
            return -1;
        }

        private static Bitmap bitmapExtra(Notification notification, String key, Context context) {
            try {
                Object value = notification.extras.get(key);
                if (value instanceof Bitmap) {
                    return (Bitmap) value;
                }
                if (Build.VERSION.SDK_INT >= 23 && value instanceof Icon) {
                    return bitmapFromIcon((Icon) value, context);
                }
            } catch (Exception ignored) {
            }
            return null;
        }

        private static Bitmap bitmapFromIcon(Icon icon, Context context) {
            if (Build.VERSION.SDK_INT < 23 || icon == null) {
                return null;
            }
            try {
                Drawable drawable = icon.loadDrawable(context);
                if (drawable instanceof BitmapDrawable) {
                    return ((BitmapDrawable) drawable).getBitmap();
                }
                int width = Math.max(1, drawable.getIntrinsicWidth());
                int height = Math.max(1, drawable.getIntrinsicHeight());
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public static class ReviveAccessibilityService extends AccessibilityService {
        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            if (event == null) {
                return;
            }
            try {
                CharSequence pkg = event.getPackageName();
                if (pkg == null || "com.codex.tabe8revive".contentEquals(pkg)) {
                    return;
                }
                String packageName = pkg.toString();
                if (isSystemNotificationPackage(packageName)) {
                    return;
                }
                if (packageName.indexOf("music") < 0 && packageName.indexOf("kugou") < 0
                        && packageName.indexOf("netease") < 0 && packageName.indexOf("qqmusic") < 0) {
                    return;
                }
                String best = "";
                if (event.getText() != null) {
                    for (int i = 0; i < event.getText().size(); i++) {
                        best = betterLyric(best, event.getText().get(i));
                    }
                }
                AccessibilityNodeInfo root = getRootInActiveWindow();
                best = scanLyricNode(root, best, 0);
                if (isUsefulLyric(best)) {
                    latestLyricText = best.trim();
                    latestLyricAt = System.currentTimeMillis();
                }
            } catch (Exception ignored) {
            }
        }

        @Override
        public void onInterrupt() {
        }

        private static String scanLyricNode(AccessibilityNodeInfo node, String current, int depth) {
            if (node == null || depth > 5) {
                return current;
            }
            current = betterLyric(current, node.getText());
            current = betterLyric(current, node.getContentDescription());
            int count = node.getChildCount();
            for (int i = 0; i < count; i++) {
                current = scanLyricNode(node.getChild(i), current, depth + 1);
            }
            return current;
        }

        private static String betterLyric(String current, CharSequence candidate) {
            if (candidate == null) {
                return current;
            }
            String value = candidate.toString().trim();
            if (!isUsefulLyric(value)) {
                return current;
            }
            if (current == null || current.length() == 0) {
                return value;
            }
            return value.length() > current.length() ? value : current;
        }

        private static boolean isUsefulLyric(String value) {
            if (value == null) {
                return false;
            }
            String text = value.trim();
            if (text.length() < 2 || text.length() > 80) {
                return false;
            }
            String lower = text.toLowerCase(Locale.US);
            String[] blocked = {
                    "酷狗", "播放", "暂停", "上一首", "下一首", "桌面", "入口", "天气", "湿度",
                    "风 ", "android", "b站", "股票", "返回", "喝水", "走动", "读书", "收拾",
                    "http", "www.", "com", "设置", "搜索", "取消", "确定"
            };
            for (int i = 0; i < blocked.length; i++) {
                if (lower.indexOf(blocked[i].toLowerCase(Locale.US)) >= 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
