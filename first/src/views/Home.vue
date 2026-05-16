<script setup lang="ts">
import { onMounted } from 'vue'
import { useVenueStore } from '../stores/venue'

const venueStore = useVenueStore()
onMounted(async () => {
  await venueStore.fetchVenues()
})
</script>

<template>
  <a-space direction="vertical" size="large" style="width: 100%">
    <a-card class="academic-card">
      <h1 class="page-title">&#26657;&#22253;&#20307;&#32946;&#39044;&#32422;&#20013;&#24515;</h1>
      <p class="page-desc">
        &#38754;&#21521;&#20840;&#20307;&#23398;&#29983;&#25552;&#20379;&#35268;&#33539;&#12289;&#36879;&#26126;&#30340;&#22330;&#39302;&#39044;&#32422;&#26381;&#21153;&#65292;
        &#25903;&#25345;&#26102;&#38388;&#20914;&#31361;&#26657;&#39564;&#21644;&#20010;&#20154;&#39044;&#32422;&#35760;&#24405;&#31649;&#29702;&#12290;
      </p>
      <a-row :gutter="16" style="margin-top: 16px">
        <a-col :xs="24" :md="8"><a-statistic :title="'\u53ef\u9884\u7ea6\u573a\u9986'" :value="venueStore.venues.length" /></a-col>
        <a-col :xs="24" :md="8"><a-statistic :title="'\u5f00\u653e\u65f6\u6bb5'" value="06:00 - 22:00" /></a-col>
        <a-col :xs="24" :md="8"><a-statistic :title="'\u652f\u6301\u9879\u76ee'" :value="'\u7fbd\u6bdb\u7403 / \u7bee\u7403 / \u6e38\u6cf3'" /></a-col>
      </a-row>
    </a-card>
  </a-space>
</template>