package com.auro.app.bluealert;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DiscoverActivity extends AppCompatActivity {


    private ListView mSearchedDeviceList;
    private Button mSearchButton;
    private boolean isDiscovering = false;
    private LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        initViews();
        onClickListeners();

    }

    private void initViews() {

        mSearchedDeviceList = (ListView) findViewById(R.id.list_discovered_devices);
        mSearchButton = (Button) findViewById(R.id.discovery_button);
        parentLayout = (LinearLayout) findViewById(R.id.parent_layout_discover);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
        parentLayout.setLayoutTransition(layoutTransition);

    }

    private void onClickListeners() {

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isDiscovering) {

                    mSearchedDeviceList.setPivotY(0);

                    if (mSearchedDeviceList.getScaleY() == 0)
                        mSearchedDeviceList.animate().scaleYBy(1).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                                mSearchedDeviceList.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {


                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

                    mSearchButton.setText(getString(R.string.button_text_stop));
                    isDiscovering = true;

                } else {

                    mSearchedDeviceList.clearAnimation();
                    mSearchedDeviceList.setPivotY(0);
                    mSearchedDeviceList.animate().scaleY(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {

                            mSearchedDeviceList.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    mSearchButton.setText(getString(R.string.button_text_search));
                    isDiscovering = false;
                }

            }
        });
    }

}
